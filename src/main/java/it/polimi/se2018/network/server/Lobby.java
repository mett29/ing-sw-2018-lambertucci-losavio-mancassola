package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Controller;
import it.polimi.se2018.model.*;
import it.polimi.se2018.network.message.MatchStartMessage;
import it.polimi.se2018.network.message.*;
import it.polimi.se2018.utils.Extractor;
import it.polimi.se2018.utils.JsonParser;

import java.io.IOException;
import java.util.*;

public class Lobby implements Observer{
    private List<String> usernames;
    private Map<String, Player> playerMap;
    private Server server;
    private Controller controller;
    private List<ParsedBoard> parsedBoards;
    private JsonParser jsonParser = new JsonParser();
    private Timer timer;
    private int timerValue = -1;

    // This Map contains the name of the player and the set of the boards between which he will choose
    private Map<String, List<ParsedBoard>> playerPatternsMap;

    // This Map contains all the lobby's player with their equivalent board's index
    private Map<String, Integer> playerWithBoard;

    Lobby(List<String> usernames, Server server) throws IOException{
        System.out.println("New lobby created");
        this.usernames = usernames;
        newPlayerMap();
        newPlayerWithBoard();
        this.server = server;
        this.parsedBoards = jsonParser.getParsedBoards();
    }

    /**
     * Start match
     * This function creates a new controller, a new match and starts the controller.
     */
    protected void startMatch() throws IOException {
        controller = new Controller(this);
        playerPatternsMap = new HashMap<>();

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
            List<Board> patterns = new ArrayList<>();
            List<String> patternNames = new ArrayList<>();

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
    public void updateOne(String username, Message message) {
        server.send(username, message);
    }

    /**
     * Send a message (update clients' match state) to every lobby's client
     * @param message Message to be broadcasted
     */
    public void updateAll(Message message){
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
    public void onReceive(Message message){
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
                // Send to the client the time reset message and restart timer for the next turn
                updateOne(message.username, new TimeResetMessage(message.username, Message.Content.TIME_RESET));
                restartTimer();
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
                    // Initialize timer value and send it to the client with the Match object
                    timerValue = 20;
                    updateAll(new MatchStartMessage(match, timerValue));
                    // Start timer for the first time
                    startTimer(controller);
                }
                break;
            case UNDO_REQUEST:
                boolean response = controller.undo(message.username);
                updateOne(message.username, new UndoResponse(true));
                break;
            case LOGIN:
                updateOne(message.username, new MatchStartMessage(controller.getMatch(), timerValue));
                break;
            case QUEUE:
                break;
            default:
                // This should'n happen
                System.out.println("Received strange message");
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
        System.out.println(chosenPattern.getName());

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

    /**
     * Timer to handle players' moves
     */
    private void startTimer(Controller controller) {
        timer = new Timer();
        timerValue = 20;
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                setInterval(controller);
            }
        }, 0, 1000);
    }
    
    private void restartTimer() {
        timerValue = 20;
    }

    private void setInterval(Controller controller) {
        System.out.println(timerValue);
        if (timerValue == 0) {
            String username = controller.getMatch().getPlayerQueue().peek().getName();
            onReceive(new PassRequest(username));
            //updateOne(username, new TimeResetMessage(username, Message.Content.TIME_RESET));
            //restartTimer();
        }
        --timerValue;
    }
}
