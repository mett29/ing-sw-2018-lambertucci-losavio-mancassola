package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Controller;
import it.polimi.se2018.model.*;
import it.polimi.se2018.network.message.MatchStartMessage;
import it.polimi.se2018.network.message.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.*;

public class Lobby implements Observer{
    private List<String> usernames;
    private Map<String, Player> playerMap;
    private Server server;
    private Controller controller;
    private List<ParsedBoard> extractedPatterns;

    // This Map contains the name of the player and the set of the boards between which he will choose
    private Map<String, List<ParsedBoard>> playerPatternsMap;

    // This Map contains all the lobby's player with their equivalent board's index
    private Map<String, Integer> playerWithBoard;

    Lobby(List<String> usernames, Server server){
        System.out.println("New lobby created");
        this.usernames = usernames;
        newPlayerMap();
        newPlayerWithBoard();
        this.server = server;

        try {
            startMatch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start match
     * This function creates a new controller, a new match and starts the controller.
     */
    private void startMatch() throws IOException {
        controller = new Controller(this);

        for (Player player : this.getPlayers()) {
            // Extract 4 boards and ask the player which one he wants to play with
            extractedPatterns = controller.extractPatterns();
            newPlayerPatternsMap(extractedPatterns);
            // Getting all the pattern's name that will be displayed to the player
            List<Board> patterns = new ArrayList<>();

            // For each extracted pattern, add corresponding board to `patterns`, which will be sent to the client
            extractedPatterns.forEach(parsedBoard -> patterns.add(patternToBoard(parsedBoard)));
            updateOne(player.getName(), new PatternRequest(player.getName(), patterns));
        }
    }

    /**
     * Initialize playerMap.
     * Create a Player object for every username in `usernames`
     */
    private void newPlayerMap(){
        playerMap = new HashMap<>();
        for(String username : usernames){
            playerMap.put(username, new Player(username));
        }
    }

    /**
     * Initialize playerWithBoard
     * Put username and the board's index
     */
    private void newPlayerWithBoard() {
        playerWithBoard = new HashMap<>();
        for (String username : usernames) {
            playerWithBoard.put(username, -1);
        }
    }

    /**
     * Initialize playerPatternMap
     * Create a ParsedBoard object for every username in 'usernames'
     */
    private void newPlayerPatternsMap(List<ParsedBoard> parsedBoards) {
        playerPatternsMap = new HashMap<>();
        for (String username : usernames) {
            playerPatternsMap.put(username, new ArrayList<>(parsedBoards));
        }
    }

    /**
     * Send a message to a specific client
     * @param username A specific client's username
     * @param message Message to be sent
     */
    public void updateOne(String username, Message message) {
        server.send(username, message);
    }

    /**
     * Send a message (update clients' match state) to every lobby's client
     * @param message Message to be broadcasted
     */
    public void updateAll(Message message){
        for(String username : usernames){
            server.send(username, message);
        }
    }

    public List<Player> getPlayers(){
        List<Player> ret = new ArrayList<>();
        for (String username : usernames) {
            ret.add(playerMap.get(username));
        }
        return ret;
    }

    /**
     * Handle message.
     * This function is called when the server receives a message from one of the lobby's clients.
     * @param message Message to be handled
     */
    public void onReceive(Message message){
        switch(message.content){
            case TOOLCARD_REQUEST:
                int toolCardIndex = ((ToolCardRequest) message).index;
                controller.activateToolcard(message.username, toolCardIndex);
                break;
            case PLAYER_MOVE:
                // Convert Message to PlayerMove and send to controller
                Match match = controller.getMatch();
                PlayerMove move = ((MoveMessage) message).payload.toPlayerMove(match.getPlayerByName(message.username), match);
                controller.handleMove(move);
                break;
            case PATTERN_RESPONSE:
                String username = message.username;
                int chosenPattern = ((PatternResponse) message).index;
                playerWithBoard.put(username, chosenPattern);
                handlePatternChoice(username, chosenPattern);
                if (playerWithBoard.size() == this.getPlayers().size()) {
                    // The match can be started
                    // Set match to new match created by controller
                    match = controller.getMatch();
                    updateAll(new MatchStartMessage(match));
                }
                break;
            default:
                // This should'n happen
                System.out.println("Received strange message");
        }
    }

    /**
     * Convert a pattern (previously parsed from file) to a Board
     * @param pattern ParsedBoard to convert
     * @return Conversion result
     */
    private Board patternToBoard(ParsedBoard pattern){
        // Getting all the color and value restrictions and create a matrix containing all of them
        Restriction[][] restrictions = new Restriction[4][5];
        for (ColorRestrictions cr : pattern.getColorRestrictions()) {
            for (int[] coords : cr.getCoords()) {
                restrictions[coords[0]][coords[1]] = new Restriction(Color.valueOf(cr.getColor().toUpperCase()));
            }
        }
        for (ValueRestrictions vr : pattern.getValueRestrictions()) {
            for (int[] coords : vr.getCoords()) {
                restrictions[coords[0]][coords[1]] = new Restriction(vr.getValue());
            }
        }
        // Create a board with the same characteristics as chosenPattern
        return new Board(restrictions, pattern.getDifficulty());
    }

    /**
     * This method creates the equivalent Board object from the loaded pattern and it gives it to the player
     * @param username Username of the player who choose the pattern
     * @param chosenIndex The index of the chosen pattern in the PatternRequest received by the client
     */
    private void handlePatternChoice(String username, int chosenIndex) {

        // Getting the ParsedBoard object according to the index
        ParsedBoard chosenPattern = playerPatternsMap.get(username).get(chosenIndex);
        System.out.println(chosenPattern.getName());

        Board chosenBoard = patternToBoard(chosenPattern);

        // Set the chosen board to the equivalent player
        playerMap.get(username).setBoard(chosenBoard);
    }

    @Override
    public void update(Observable match, Object o) {
        updateAll(new MatchStateMessage((Match) match));
    }
}
