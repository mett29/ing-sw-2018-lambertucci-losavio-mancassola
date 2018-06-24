package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.server.Lobby;
import it.polimi.se2018.utils.*;

import java.util.*;

class GameManager implements Comparator<Score>{
    private Match match;
    private RoundManager roundManager;

    /**
     * Constructor
     * Create and initialize the Match
     * @param lobby
     */
    GameManager(Lobby lobby) {
        this.match = new Match(lobby.getPlayers(), extractToolCards(), extractPublicObjCards(), lobby);
        extractPrivateObjCard();
        this.roundManager = new RoundManager(match);
    }

    /**
     * Creates a deck of private objective cards. Sets a randomly extracted private objective card to all players.
     */
    private void extractPrivateObjCard() {
        Extractor<PrivateObjCard> privateObjCardDeck = new Extractor<>();

        for(Color color : Color.values())
            privateObjCardDeck.insert(new PrivateObjCard(color));

        for(Player player : match.getPlayers())
            player.setPrivateObjCard(privateObjCardDeck.extract());
    }

    /**
     * Creates a deck of toolcards. Returns a set of 3 cards.
     * @return 3 toolcards randomly extracted.
     */
    private static ToolCard[] extractToolCards() {
        Extractor<ToolCard> toolCardDeck = new Extractor<>();

        for(int n = 0; n < 12; n++)
            toolCardDeck.insert(new ToolCard(n));

        List<ToolCard> toolCardExtracted = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            toolCardExtracted.add(toolCardDeck.extract());

        return toolCardExtracted.toArray(new ToolCard[3]);
    }

    /**
     * Creates a deck of public objective cards. Return a set of 3 cards.
     * @return 3 public objective cards randomly extracted.
     */
    private static PublicObjCard[] extractPublicObjCards() {
        Extractor<PublicObjCard> publicObjCardDeck = new Extractor<>();

        for(int n = 0; n < 10; n++)
            publicObjCardDeck.insert(new PublicObjCard(n));

        List<PublicObjCard> publicObjCardExtracted = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            publicObjCardExtracted.add(publicObjCardDeck.extract());

        return publicObjCardExtracted.toArray(new PublicObjCard[3]);
    }

    /**
     * Calculates the final score of every player based on PrivateObjectiveCards, PublicObjectiveCards, tokens and empty cells.
     */
    void calculateScore() {
        for(Player player : match.getPlayers()) {
            int privCards, publCards = 0, tok, empty;

            privCards = player.getPrivateObjCard().getBonus(player.getBoard());

            for (PublicObjCard publicObjCard : match.getPublicObjCards())
                publCards += publicObjCard.getBonus(player.getBoard());

            tok = player.getToken();

            empty = 20 - player.getBoard().countDice();

            match.setScore(player, new Score(privCards, publCards, tok, empty));
        }
    }

    /**
     * Declares the winner of the match. The player will have the flag 'winner' setted to true.
     */
    void declareWinner() {
        List<Player> players = new ArrayList<>();
        LinkedList<Player> q = compareQueue();

        players.addAll(match.getPlayers());

        while(players.size() > 1) {
            int finalCompare = compare(match.getScore(players.get(0)), match.getScore(players.get(1)));

            if(finalCompare == 0)
                finalCompare = q.indexOf(match.getPlayers().get(0)) - q.indexOf(match.getPlayers().get(1));

            if(finalCompare < 0)
                players.remove(match.getPlayers().get(0));
            else
                players.remove(match.getPlayers().get(1));
        }

        players.get(0).setWinner(true);
    }

    /**
     * Handle the player's move.
     * @param move of the player
     * @return true if all moves are finished, false otherwise
     */
    boolean handleMove(PlayerMove move) {
        return roundManager.handleMove(move);
    }

    /**
     * Activate one of the 3 toolcards.
     * Checks if the username coincide with the first player's name of the queue
     * Checks if it's possible to use the toolcard and if the player has enough tokens. Increase the cost of the toolcard if activated.
     * @param username of the player
     * @param toolCardId index of toolcard selected
     * @return true if successfully activated
     */
    boolean activateToolcard(String username, int toolCardId) {
        return roundManager.activateToolcard(username, toolCardId);
    }

    /**
     * Activate pick_die move
     * Checks if it's possible to make a move.
     * @param username of the player
     * @return true if successfully activated
     */
    boolean activateNormalMove(String username) {
        return roundManager.activateNormalMove(username);
    }

    /**
     * Pass the current player's turn
     * Checks if it's possible to pass the turn
     * @param username of the player
     * @return true if successfully passed the turn
     */
    boolean passTurn(String username) {
        boolean roundFinished = roundManager.passTurn(username);

        if(match.getRoundTracker().getCurrentSize() == 10){
            return true;
        } else {
            if(roundFinished) roundManager.newRound();
            //match.notifyObservers();
            return false;
        }
    }

    /**
     * Undo the current operation
     * @param username of the current player
     * @return true if the state is not YOUR_TURN, false otherwise
     */
    boolean undo(String username) {
        return roundManager.undo(username);
    }

    /**
     * @return the current match
     */
    Match getMatch() {
        return match;
    }

    /**
     * Creates a new round passing the first turn to the player next to him (clockwise procedure).
     */
    private LinkedList<Player> compareQueue(){
        LinkedList<Player> playerQueue = new LinkedList<>();

        List<Player> playerList = match.getPlayers();
        int turnNumber = match.getRoundTracker().getCurrentSize();
        int playerSize = playerList.size();
        Queue<Integer> playerIndexes = new LinkedList<>();

        for(int i = 0; i < playerSize; i++){
            playerIndexes.add((i + turnNumber) % playerSize);
        }

        Integer[] indexes = playerIndexes.toArray(new Integer[playerSize]);

        for(int i = 0; i < 2 * playerIndexes.size(); i++){
            if(i < playerIndexes.size()){
                playerQueue.add(playerList.get(indexes[i]));
            } else {
                playerQueue.add(playerList.get(indexes[2 * playerSize - i - 1]));
            }
        }
        return playerQueue;
    }

    @Override
    public int compare(Score o1, Score o2) {
        int overallCompare = o1.getOverallScore() - o2.getOverallScore();

        if(overallCompare != 0)
            return overallCompare;

        int privateObjectiveCompare = o1.getValues()[0] - o2.getValues()[0];

        if(privateObjectiveCompare != 0)
            return privateObjectiveCompare;

        int tokenCompare = o1.getValues()[2] - o2.getValues()[2];

        if(tokenCompare != 0)
            return tokenCompare;

        return 0;
    }
}
