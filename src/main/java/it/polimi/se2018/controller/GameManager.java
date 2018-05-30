package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.server.Lobby;
import it.polimi.se2018.utils.Extractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GameManager {
    private Match match;
    private RoundManager roundManager;
    private JsonParser jsonParser = new JsonParser();

    GameManager(Lobby lobby) throws IOException {
        this.match = new Match(lobby.getPlayers(), extractToolCards(), extractPublicObjCards(), lobby);
        extractPrivateObjCard();
        List<ParsedBoard> parsedBoards = jsonParser.getParsedBoards();
        choosePattern(lobby, parsedBoards);
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
     * @throws IOException
     */
    private List<ParsedBoard> extractPatterns(List<ParsedBoard> parsedBoards) throws IOException {
        Extractor<ParsedBoard> parsedBoardExtractor = new Extractor<>();
        for (ParsedBoard pb : parsedBoards) {
            parsedBoardExtractor.insert(pb);
        }
        List<ParsedBoard> parsedBoardExtracted = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            parsedBoardExtracted.add(parsedBoardExtractor.extract());
        }
        return parsedBoardExtracted;
    }

    /**
     * This method extracts a set of 4 patterns for each player and ask him to choose one
     * @param lobby, containing all the players
     * @param parsedBoards, containing all the parsed boards, load once in the constructor
     * @throws IOException
     */
    private void choosePattern(Lobby lobby, List<ParsedBoard> parsedBoards) throws IOException {
        List<Player> lobbyPlayers = lobby.getPlayers();
        for (Player player : lobbyPlayers) {
            List<ParsedBoard> extractedPatterns = extractPatterns(parsedBoards);
            // Ask the player what pattern he wants
            // TODO: asking the player
            // Assuming the player has already chosen the pattern
            ParsedBoard chosenPattern = extractedPatterns.get(0);
            // Create a board with the same characteristics as chosenPattern
            // Getting all the color and value restrictions and create a matrix containing all of them
            Restriction[][] restrictions = new Restriction[4][5];
            for (ColorRestrictions cr : chosenPattern.getColorRestrictions()) {
                for (int[] coords : cr.getCoords()) {
                    restrictions[coords[0]][coords[1]] = new Restriction(Color.valueOf(cr.getColor().toUpperCase()));
                }
            }
            for (ValueRestrictions vr : chosenPattern.getValueRestrictions()) {
                for (int[] coords : vr.getCoords()) {
                    restrictions[coords[0]][coords[1]] = new Restriction(vr.getValue());
                }
            }
            Board chosenBoard = new Board(restrictions, chosenPattern.getDifficulty());
            player.setBoard(chosenBoard);
        }
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
     * Handle the player's move through RoundManager and checks if the match is finished.
     * @param move of the player
     * @return true if the match is finished, false otherwise
     */
    boolean handleMove(PlayerMove move) {
        boolean roundFinished = roundManager.handleMove(move);
        if(roundFinished) roundManager.newRound();
        if(match.getRoundTracker().getCurrentSize() == 10){
            return true;
        } else {
            match.notifyObservers();
            return false;
        }
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
        if(match.getPlayerQueue().peek().getName().equals(username))
            return roundManager.activateToolcard(toolCardId);
        else
            return false;
    }
}
