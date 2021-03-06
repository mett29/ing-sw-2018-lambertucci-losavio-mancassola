package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Configuration;
import it.polimi.se2018.controller.Controller;
import it.polimi.se2018.model.*;
import it.polimi.se2018.network.message.MatchStartMessage;
import it.polimi.se2018.network.message.*;
import it.polimi.se2018.utils.Extractor;
import it.polimi.se2018.utils.JsonParser;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the Lobby, where all the clients are "stored" before becoming real players.
 * Once the lobby reaches the right number of players, the game starts.
 * In this class the patterns written in the file are loaded and parsed and sent to the users, so that they can choose one.
 * It also handles the client/server communication with messages.
 * It observes the Match.
 * @author MicheleLambertucci, mett29
 */
public class Lobby implements Observer{
    private Logger logger;

    private List<String> usernames;
    private Map<String, Player> playerMap;
    private Server server;
    private Controller controller;
    private List<ParsedBoard> parsedBoards;
    private JsonParser jsonParser;
    private CountdownTimer timer;

    // This Map contains the name of the player and the set of the boards between which he will choose
    private Map<String, List<ParsedBoard>> playerPatternsMap;

    // This Map contains all the lobby's player with their equivalent board's index
    private Map<String, Integer> playerWithBoard;

    Lobby(List<String> usernames, Server server) {
        logger = Logger.getLogger("Lobby");

        logger.log(Level.INFO, "New lobby created");
        this.usernames = usernames;
        newPlayerMap();
        newPlayerWithBoard();
        this.server = server;

        try {
            jsonParser = new JsonParser();
            this.parsedBoards = jsonParser.getParsedBoards();
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * Start match
     * This function creates a new controller, a new match and starts the controller.
     */
    void startMatch() {
        controller = new Controller(this);
        playerPatternsMap = new HashMap<>();

        //setup timer
        timer = new CountdownTimer(Configuration.getInstance().getInGameTimer(),
                () -> {
                    Match match = controller.getMatch();
                    if(match != null && match.getPlayerQueue() != null && match.getPlayerQueue().peek() != null) {
                        String playerToPass = match.getPlayerQueue().peek().getName();
                        onReceive(new PassRequest(playerToPass));
                    }
                },
                () -> updateAll(new TimeResetMessage()),
                () -> { /*do nothing*/ }
                );

        Extractor<ParsedBoard> parsedBoardExtractor = new Extractor<>();
        for (ParsedBoard pb : this.parsedBoards) {
            parsedBoardExtractor.insert(pb);
        }

        for (Player player : this.getPlayers()) {
            // Extract 4 boards and ask the player which one he wants to play with
            List<ParsedBoard> extractedPatterns = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                extractedPatterns.add(parsedBoardExtractor.extract());
            }
            // This line associates each username to the related 4 extracted patterns
            playerPatternsMap.put(player.getName(), extractedPatterns);
            // Getting all the pattern's name that will be displayed to the player
            ArrayList<Board> patterns = new ArrayList<>();
            ArrayList<String> patternNames = new ArrayList<>();

            // For each extracted pattern, add corresponding board to `patterns`, which will be sent to the client
            extractedPatterns.forEach(parsedBoard -> patterns.add(patternToBoard(parsedBoard)));
            extractedPatterns.forEach(parsedBoard -> patternNames.add(parsedBoard.getName()));
            updateOne(player.getName(), new PatternRequest(player.getName(), patterns, patternNames, player.getPrivateObjCard()));
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
     * It will contain username and the board's index
     */
    private void newPlayerWithBoard() {
        playerWithBoard = new HashMap<>();
    }

    /**
     * Send a message to a specific client
     * @param username A specific client's username
     * @param message Message to be sent
     */
    private void updateOne(String username, Message message) {
        server.send(username, message);
    }

    /**
     * Send a message (update clients' match state) to every lobby's client
     * @param message Message to be broadcasted
     */
    private void updateAll(Message message){
        for(String username : usernames){
            if(!getMatch().getPlayerByName(username).isDisconnected())
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
    void onReceive(Message message){
        switch(message.content){
            case TOOLCARD_REQUEST:
                int toolCardIndex = ((ToolCardRequest) message).index;
                controller.activateToolcard(message.username, toolCardIndex);
                break;
            case NORMAL_MOVE:
                controller.activateNormalMove(message.username);
                break;
            case PASS:
                controller.passTurn(message.username);
                // restart timer for the next turn
                timer.reset();
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
                    // Start timer and send infos to clients within the MatchStartMessage
                    timer.start();
                    logger.log(Level.INFO, "Match started");
                    updateAll(new MatchStartMessage(match, timer.getDuration()));
                }
                break;
            case UNDO_REQUEST:
                boolean response = controller.undo(message.username);
                updateOne(message.username, new UndoResponse(response));
                break;
            case LOGIN:
                updateOne(message.username, new MatchStartMessage(controller.getMatch(), timer.getDuration()));
                break;
            case QUEUE:
                break;
            default:
                // This shouldn't happen
                logger.log(Level.WARNING, "Received malformed message");
        }
    }

    public Match getMatch() {
        return controller.getMatch();
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

        Board chosenBoard = patternToBoard(chosenPattern);

        // Set the chosen board to the equivalent player
        playerMap.get(username).setBoard(chosenBoard);
    }

    @Override
    public void update(Observable match, Object o) {
        updateAll(new MatchStateMessage((Match) match));
        if(((Match) match).isFinished()) {
            timer.cancel();
            server.deleteLobbyByPlayerNames(((Match) match).getPlayers());
        }
    }
}
