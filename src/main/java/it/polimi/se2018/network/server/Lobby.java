package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Controller;
import it.polimi.se2018.model.*;
import it.polimi.se2018.network.message.*;

import java.io.IOException;
import java.util.*;

public class Lobby implements Observer{
    private List<String> usernames;
    private Map<String, Player> playerMap;
    private Server server;
    private Controller controller;
    private List<ParsedBoard> extractedPatterns;
    // 'playerWithBoard' contains all the lobby's player with their equivalent board
    private Map<String, String> playerWithBoard;

    Lobby(List<String> usernames, Server server){
        System.out.println("New lobby created");
        this.usernames = usernames;
        newPlayerMap();
        this.server = server;
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
            // Getting all the pattern's name that will be displayed to the player
            List<String> patternNames = new ArrayList<>();
            for (ParsedBoard pb : extractedPatterns) {
                patternNames.add(pb.getName());
            }
             updateOne(player.getName(), new PatternRequest(player.getName(), patternNames));
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
                String chosenPatternName = ((PatternResponse) message).patternName;
                handlePatternChoice(username, chosenPatternName);
                playerWithBoard.put(username, chosenPatternName);
                if (playerWithBoard.size() == 4) {
                    // The match can be started
                    // TODO
                }
                break;
            default:
                // This should'n happen
                System.out.println("Received strange message");
        }
    }

    /**
     * This method creates the equivalent Board object from the loaded pattern and it gives it to the player
     * @param username Username of the player who choose the pattern
     * @param chosenPatternName The name of the chosen pattern
     */
    private void handlePatternChoice(String username, String chosenPatternName) {

        // Getting the ParsedBoard object (the chosen pattern) from its name
        ParsedBoard chosenPattern = new ParsedBoard();
        for (ParsedBoard extractedPattern : extractedPatterns) {
            if (extractedPattern.getName().equals(chosenPatternName)) {
                chosenPattern = extractedPattern;
            }
        }

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
        // Create a board with the same characteristics as chosenPattern
        Board chosenBoard = new Board(restrictions, chosenPattern.getDifficulty());

        // Set the chosen board to the equivalent player
        playerMap.get(username).setBoard(chosenBoard);
    }

    @Override
    public void update(Observable match, Object o) {
        updateAll(new MatchStateMessage((Match) match));
    }
}
