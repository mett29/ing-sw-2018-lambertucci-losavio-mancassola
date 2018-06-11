package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.server.Lobby;
import it.polimi.se2018.network.server.ParsedBoard;
import it.polimi.se2018.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GameManager {
    private Match match;
    private RoundManager roundManager;
    private List<ParsedBoard> parsedBoards;
    private JsonParser jsonParser = new JsonParser();

    /**
     * Constructor
     * Create and initialize the Match
     * @param lobby
     * @throws IOException
     */
    GameManager(Lobby lobby) throws IOException {
        this.match = new Match(lobby.getPlayers(), extractToolCards(), extractPublicObjCards(), lobby);
        extractPrivateObjCard();
        this.parsedBoards = jsonParser.getParsedBoards();
        match.notifyObservers();
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
     * @return 4 parsed boards between which the player will choose
     */
    protected List<ParsedBoard> extractPatterns() {
        Extractor<ParsedBoard> parsedBoardExtractor = new Extractor<>();
        for (ParsedBoard pb : this.parsedBoards) {
            parsedBoardExtractor.insert(pb);
        }
        List<ParsedBoard> parsedBoardExtracted = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            parsedBoardExtracted.add(parsedBoardExtractor.extract());
        }
        return parsedBoardExtracted;
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

            match.notifyObservers();
        }
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
            match.notifyObservers();
            return false;
        }
    }

    /**
     * @return the current match
     */
    public Match getMatch() {
        return match;
    }
}
