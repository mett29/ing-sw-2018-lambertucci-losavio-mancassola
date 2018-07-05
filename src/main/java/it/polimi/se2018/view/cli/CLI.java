package it.polimi.se2018.view.cli;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.*;
import it.polimi.se2018.network.message.PatternRequest;
import it.polimi.se2018.network.message.UndoResponse;
import it.polimi.se2018.view.ViewInterface;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static it.polimi.se2018.view.cli.InputManager.SELECTION_MAP;
import static java.lang.Integer.max;

/**
 * This class represent the Command-Line Interface
 * @author MicheleLambertucci
 */
public class CLI implements ViewInterface {
    private PlayerState state;
    private Match match;
    private Client client;

    private static final int PLAYER_HEIGHT = 11;
    private static final int PLAYER_WIDTH = 47;
    private static final int CARD_WIDTH = 26;

    public CLI(Client client){ this.client = client; }

    /**
     * Launches the Command-Line Interface
     */
    public void launch(){
        askLogin();
        askTypeOfConnection();
    }

    /**
     * Asks the player's username to use during the match
     */
    private void askLogin(){
        System.out.println("    ┏━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("    ┃       = LOGIN =       ┃");
        System.out.println("    ┠───────────────────────┨");
        System.out.println("    ┃  Immetti il tuo       ┃");
        System.out.println("    ┃  username.            ┃");
        System.out.println("    ┗━━━━━━━━━━━━━━━━━━━━━━━┛");
        System.out.println();

        Scanner sc = new Scanner(System.in);
        String username = sc.next();
        while (username.equals("admin")) {
            System.out.println("Non puoi scegliere 'admin' come username");
            username = sc.next();
        }
        client.setUsername(username);
    }

    /**
     * Asks to the player what type of connection he wants to use.
     * Selection 0: Socket
     * Selection 1: RMI
     */
    private void askTypeOfConnection(){
        List<String> connections = new ArrayList<>();
        connections.add("Socket");
        connections.add("RMI");

        makeSelection(connections, selected -> {
            if(selected == 0){
                client.setConnection(false);
            } else {
                client.setConnection(true);
            }
            try {
                client.connect();
            } catch (Exception e) {
                client.onConnectionError(e);
            }
        });


    }

    /**
     * Sets the current match to the CLI and shows the match
     * @param match object to set
     * @param timerValue not used in CLI
     */
    @Override
    public void onMatchStart(Match match, int timerValue) {
        this.match = match;
        displayMatch(match);
        Player me = getMyself(match);
        if(me != null && me.getState().get() == EnumState.YOUR_TURN){
            onYourTurnState();
        }
    }

    /**
     * Four boards will be shown and the player must choose one
     * @param message object received
     */
    @Override
    public void askPattern(PatternRequest message) {
        List<String> selections = new ArrayList<>();

        System.out.println("La tua carta obiettivo privata: " + message.privateObjCard.getColor());
        System.out.println();

        int iteration = 0;
        for(Board b : message.boards){
            System.out.println("[ -- " + message.boardNames.get(iteration) + " | Diff.: " + b.getBoardDifficulty() + " -- ]");
            printLines(Stringifier.toStrings(b));

            selections.add(message.boardNames.get(iteration));

            iteration++;
        }

        makeSelection(selections, selection ->
            client.sendPatternResponse(selection)
        );
    }

    /**
     * Response of undo request
     * @param message object received
     */
    @Override
    public void displayUndoMessage(UndoResponse message) {
        if(message.ok)
            System.out.println("La tua mossa è stata annullata");
        else
            System.out.println("Non puoi annullare la mossa in questo momento");
    }

    /**
     * Shows the current match in player's console
     * @param match object to read
     */
    private void displayMatch(Match match){
        System.out.println("+ ROUND TRACKER +                                      + DRAFT POOL +");
        printLines(Stringifier.display2(Stringifier.diceContainerToStrings(match.getRoundTracker(), false, null), Stringifier.diceContainerToStrings(match.getDraftPool(), false, null)));
        displayPlayers(match.getPlayers().toArray(new Player[0]));
        System.out.println("+ TOOLCARDS +");
        displayCards(match.getToolCards());
        System.out.println("+ PUBLIC OBJECTIVE CARDS +");
        displayCards(match.getPublicObjCards());
        if(match.getPlayerQueue().peek().getPickedDie() != null)
            System.out.println(match.getPlayerQueue().peek().getPickedDie());
    }

    /**
     * Shows a set of 3 cards
     * @param cards object to read
     */
    private static void displayCards(Card[] cards){
        if(cards.length != 3)
            throw new RuntimeException("cards.length must be 3");
        printLines(Stringifier.display2(Stringifier.display2(Stringifier.toStrings(cards[0]), Stringifier.toStrings(cards[1])), Stringifier.toStrings(cards[2])));
    }

    /**
     * @param lines object to print
     */
    private static void printLines(String[] lines){
        for (String line : lines) {
            System.out.println(line);
        }
    }

    /**
     * Shows all players of the match in the console
     * @param players object to read
     */
    private void displayPlayers(Player[] players){
        int rows = players.length / 2;
        for (int i = 0; i < rows; i++) {
            printLines(Stringifier.display2(playerToStrings(players[i*2]), playerToStrings(players[i*2+1])));
        }
        if(players.length % 2 == 1){
            printLines(playerToStrings(players[players.length - 1]));
        }
    }

    /**
     * Transforms player's information into string
     * @param player object to read
     * @return the stringfied player's info
     */
    private String[] playerToStrings(Player player){
        String[] boardString = Stringifier.toStrings(player.getBoard());

        int tokens = player.getToken();
        List<String> ret = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < tokens; i++){
            buffer.append("*");
        }
        String tokenString = buffer.toString();

        String playerDisconnected = player.isDisconnected() ? " (DISCONN.)" : "";

        String[] firstCol = new String[]{
                "Player:",
                "" + player.getName(),
                player.getName().equals(client.getUsername()) ? "  (YOU)" : "" + playerDisconnected,
                player.getState().get().toString(),
                player.getPickedDie() == null ? "" : "Hand: " + player.getPickedDie().toString(),
                "Tokens:",
                " " + tokenString,
                player.getName().equals(client.getUsername()) ? "Private: " + player.getPrivateObjCard().getColor().toString() : ""
        };


        buffer = new StringBuilder();
        buffer.append("╔");
        for(int i = 0; i < PLAYER_WIDTH - 2; i++){
            buffer.append("═");
        }
        buffer.append("╗");
        ret.add(buffer.toString());
        buffer = new StringBuilder();
        for(int i = 0; i < PLAYER_HEIGHT - 2; i++){
            buffer.append("║   ");
            if(i < firstCol.length){
                String tmp = firstCol[i];
                int padding = 15 - tmp.length();
                buffer.append(tmp);
                for(int h = 0; h < padding; h++){
                    buffer.append(" ");
                }
            } else {
                int padding = 15;
                for(int h = 0; h < padding; h++){
                    buffer.append(" ");
                }
            }
            buffer.append(boardString[i]);
            buffer.append(" ║");
            ret.add(buffer.toString());
            buffer = new StringBuilder();
        }
        buffer.append("╚");
        for(int i = 0; i < PLAYER_WIDTH - 2; i++){
            buffer.append("═");
        }
        buffer.append("╝");
        ret.add(buffer.toString());
        return ret.toArray(new String[PLAYER_HEIGHT]);
    }

    /**
     * Called when a player's state is changed
     * Closes the current input
     * @param oldState object to set (for REPEAT)
     * @param newState object to set
     */
    private void onChangeState(PlayerState oldState, PlayerState newState){
        InputManager.closeInput();
        switch(newState.get()){
            case IDLE:
                break;
            case PICK:
                onPickState(newState);
                break;
            case VALUE:
                onValueState();
                break;
            case UPDOWN:
                onUpDownState();
                break;
            case YESNO:
                onYesNoState();
                break;
            case REPEAT:
                onRepeatState(oldState);
                break;
            case YOUR_TURN:
                onYourTurnState();
        }
    }

    /**
     * Shows the current player's options
     * Called when the player's state is YOUR_TURN
     */
    private void onYourTurnState() {
        System.out.println("    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("    ┃              = TOCCA A TE =            ┃");
        System.out.println("    ┠────────────────────────────────────────┨");
        System.out.println("    ┃  Cosa vuoi fare?                       ┃");
        if(match.getPlayerByName(client.getUsername()).getPossibleActions().contains(PossibleAction.PICK_DIE))
            System.out.println("    ┃  0 - Prendi un dado dalla Draft Pool   ┃");
        if(match.getPlayerByName(client.getUsername()).getPossibleActions().contains(PossibleAction.ACTIVATE_TOOLCARD))
            System.out.println("    ┃  1 - Usa una ToolCard                  ┃");
        if(match.getPlayerByName(client.getUsername()).getPossibleActions().contains(PossibleAction.PASS_TURN))
            System.out.println("    ┃  2 - Passa                             ┃");
        System.out.println("    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

        Pattern pattern = Pattern.compile("[0-2]");
        InputManager.ask(pattern,
                Integer::parseInt,
                (Integer in) -> {
                    switch(in){
                        case 0:
                            client.activateNormalMove();
                            break;
                        case 1:
                            askToolCardActivation();
                            break;
                        case 2:
                            client.pass();
                            break;
                        default:
                            throw new UnsupportedOperationException();
                    }
                }
        );
    }

    /**
     * Shows to the player all possible selectable toolcards
     * Called when the player's choose '1' in YOUR_TURN
     */
    private void askToolCardActivation() {
        displayCards(match.getToolCards());
        List<String> selectables = new ArrayList<>();
        Arrays.stream(match.getToolCards()).map(ToolCard::getTitle).forEachOrdered(selectables::add);
        makeSelection(selectables, selectedIndex -> client.activateToolCard(selectedIndex));
    }

    /**
     * The player will repeat the last action
     * @param oldState object to set
     */
    private void onRepeatState(PlayerState oldState) {
        System.out.println("Mossa non valida! Riprova.");
        setState(oldState);
    }

    /**
     * Shows to the player the possible actions: +1 or -1
     * Called when the player's state is UPDOWN
     */
    private void onUpDownState() {
        System.out.println("Do you want to add or subtract 1 to the die value?");
        List<String> selections = Arrays.stream(new String[]{"+1", "-1"}).collect(Collectors.toList());
        makeSelection(selections, (selection) -> client.sendMove(new UpDownMove(selection == 0)));
    }

    /**
     * Shows to the player the possible actions: Yes or No
     * Called when the player's state is YESNO
     */
    private void onYesNoState() {
        System.out.println("Do you want to continue?");
        List<String> selections = Arrays.stream(new String[]{"Yes", "No"}).collect(Collectors.toList());
        makeSelection(selections, (selection) -> client.sendMove(new YesNoMove(selection == 0)));
    }

    /**
     * Shows to the player the possible actions: 1 .. 6 value
     * Called when the player's state is VALUE
     */
    private void onValueState() {
        System.out.println("Now pick a value from 1 to 6");

        List<Integer> selections = new ArrayList<>();
        selections.add(1);
        selections.add(2);
        selections.add(3);
        selections.add(4);
        selections.add(5);
        selections.add(6);

        InputManager.ask(selections, selected ->
            client.sendMove(new ValueMove(selected))
        );
    }

    /**
     * Shows to the player the container or board to use
     * Called when the player's state is PICK
     * @param newState object to set
     */
    private void onPickState(PlayerState newState){
        PickState pState= (PickState) newState;


        switch(pState.getActiveContainers().toArray(new Component[1])[0]){
            case BOARD:
                pickBoard(match.getPlayerByName(client.getUsername()).getBoard(), pState.getCellStates());
                break;
            case DRAFTPOOL:
                pickDiceContainer(match.getDraftPool(), pState.getCellStates(), true);
                break;
            case ROUNDTRACKER:
                pickDiceContainer(match.getRoundTracker(), pState.getCellStates(), false);
                break;
            default:
                // do nothing
        }
    }

    /**
     * Shows the dice container to the player
     * @param diceContainer DRAFTPOOL or ROUNDTRACKER
     * @param cellStates FULL, EMPTY and/or NEAR
     * @param isDraftPool boolean to set
     */
    private void pickDiceContainer(DiceContainer diceContainer, Set<CellState> cellStates, boolean isDraftPool) {
        printLines(Stringifier.diceContainerToStrings(diceContainer, true, cellStates));
        System.out.println(Stringifier.pickContainerMessage(cellStates));

        InputManager.askDiceContainer(diceContainer, cellStates, isDraftPool, client);
    }

    /**
     * Shows the board to the player
     * @param board object to read
     * @param cellStates FULL, EMPTY and/or NEAR
     */
    private void pickBoard(Board board, Set<CellState> cellStates) {
        String buffer = "Now s" +
                Stringifier.toString(cellStates) +
                " (type the corresponding character)";
        System.out.println(buffer);
        printLines(Stringifier.boardToStrings(board, true, cellStates));

        InputManager.askBoard(board, cellStates, client);
    }

    /**
     * Makes all possible selection based on 'selectables'
     * @param selectables object to read
     * @param onSelected selection to read
     */
    private void makeSelection(List selectables, Consumer<Integer> onSelected){
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < selectables.size(); i++){
            buffer.append(i);
            buffer.append(": ");
            buffer.append(selectables.get(i).toString());
            buffer.append("    ");
        }
        System.out.println(buffer.toString());

        InputManager.ask(selectables, onSelected);
    }

    /**
     * Returns the owner of the client
     * @param m object to read
     * @return player
     */
    private Player getMyself(Match m){
        return m.getPlayers().stream()
                .filter(p -> p.getName().equals(client.getUsername()))
                .findFirst().orElse(null);
    }

    /**
     * Checks if the player is connected successfully
     * If the nickname is already taken, launch() will be called again
     * @param isOk boolean to read
     */
    public void onConnect(boolean isOk){
        if (isOk) {
            System.out.println("Login successful.");
            System.out.println("Press ENTER to enter the queue.");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            client.sendQueueRequest();
        } else {
            System.out.println("Username already exists.");
            launch();
        }
    }

    /**
     * Shows the match to the player when the match state is changed
     * Displays the scores if the match is finished
     * @param newMatch object to read
     */
    private void onNewMatchState(Match newMatch) {
        if(newMatch.isFinished()){
            displayScores(newMatch);
        } else {
            displayMatch(newMatch);
            Player me = getMyself(newMatch);
            if (me != null) {
                setState(me.getState());
            }
        }
    }

    /**
     * Shows the scores to all players
     * @param newMatch object to read
     */
    private void displayScores(Match newMatch) {
        for(Player p : newMatch.getPlayers()) {
            printLines(Stringifier.scoreToStrings(p, newMatch.getScore(p)));
        }
    }

    /**
     * Shows to the player the response of the toolcard activation
     * In case the player doesn't have enough tokens, the toolcard won't be activated
     * @param isOk boolean to read
     */
    public void displayToolcardActivationResponse(boolean isOk){
        if(!isOk) {
            System.out.println("Non sono disponibili abbastanza token per attivare la Toolcard");
        }
    }

    /**
     * Sets a new state to the player
     * @param state object to set
     */
    public void setState(PlayerState state) {
        PlayerState oldState = this.state;
        this.state = state;
        onChangeState(oldState, state);
    }

    /**
     * Updates the current match
     * @param newMatch object to set
     */
    public void updateMatch(Match newMatch) {
        match = newMatch;
        onNewMatchState(match);
    }

    /**
     * Shows connection error to the player if there is any connection issue
     * @param e exception to read
     */
    @Override
    public void displayConnectionError(Exception e) {
        System.out.println("    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("    ┃         ERRORE DI CONNESSIONE       ┃");
        System.out.println("    ┠─────────────────────────────────────┨");
        System.out.println("    ┃  Si è  verificato  un  errore  di   ┃");
        System.out.println("    ┃  connessione.  L'applicazione  si   ┃");
        System.out.println("    ┃  spegnerà.                          ┃");
        System.out.println("    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
    }

    @Override
    public void resetTimer() {
        // do nothing
    }

    /**
     * This class represents all information stringfied
     */
    public static class Stringifier{
        private Stringifier(){}

        private static String[] toStrings(Card card){
            String description = card.getDescription();
            String title = card.getTitle();

            List<String> ret = new ArrayList<>();

            StringBuilder buffer = new StringBuilder();
            buffer.append("╭─");
            for (int i = 0; i < CARD_WIDTH; i++) {
                buffer.append("─");
            }
            buffer.append("─╮");
            ret.add(buffer.toString());

            int i;
            for(i = 0; i < title.length() - CARD_WIDTH; i += CARD_WIDTH){
                ret.add("│ " + title.substring(i, i + CARD_WIDTH) + " │");
            }
            int padding = CARD_WIDTH - title.substring(i).length() ;
            buffer = new StringBuilder();
            buffer.append("│ ");
            buffer.append(title.substring(i));
            for (int j = 0; j < padding; j++) {
                buffer.append(" ");
            }
            buffer.append(" │");
            ret.add(buffer.toString());

            buffer = new StringBuilder();
            buffer.append("│ ");
            for (int j = 0; j < CARD_WIDTH; j++) {
                buffer.append(" ");
            }
            buffer.append(" │");
            ret.add(buffer.toString());

            for(i = 0; i < description.length() - CARD_WIDTH; i += CARD_WIDTH){
                ret.add("│ " + description.substring(i, i + CARD_WIDTH) + " │");
            }

            padding = CARD_WIDTH - description.substring(i).length();
            buffer = new StringBuilder();
            buffer.append("│ ");
            buffer.append(description.substring(i));
            for (int j = 0; j < padding; j++) {
                buffer.append(" ");
            }
            buffer.append(" │");
            ret.add(buffer.toString());

            buffer = new StringBuilder();
            buffer.append("╰─");
            for(i = 0; i < CARD_WIDTH; i++){
                buffer.append("─");
            }
            buffer.append("─╯");
            ret.add(buffer.toString());

            return ret.toArray(new String[0]);
        }

        /**
         * Creates a String[] array which represents a contrainer that can be printed on console.
         * @param container object to read
         * @param printSelectors boolean to read
         * @param cellStates FULL, EMPTY and/or NEAR
         * @return "Stringfied" version of the container.
         */
        private static String[] diceContainerToStrings(DiceContainer container, boolean printSelectors, Set<CellState> cellStates){
            List<String> ret = new ArrayList<>();

            int maxSize = container.getMaxSize();
            StringBuilder buffer = new StringBuilder();
            buffer.append("╔");
            if(printSelectors){
                // print selection character for each row accepted by cellState
                for(int i = 0; i < maxSize - 1; i++){
                    if(acceptedCell(container, i, cellStates)) {
                        buffer.append("══");
                        buffer.append(SELECTION_MAP.charAt(i));
                        buffer.append("═╤");
                    } else {
                        buffer.append("════╤");
                    }
                }

                // print last cell
                if(acceptedCell(container, maxSize - 1, cellStates)) {
                    buffer.append("══");
                    buffer.append(SELECTION_MAP.charAt(maxSize - 1));
                    buffer.append("═╗");
                } else {
                    buffer.append("════╗");
                }

            } else {
                for (int i = 0; i < maxSize - 1; i++) {
                    buffer.append("════╤");
                }
                // print last cell
                buffer.append("════╗");
            }

            ret.add(buffer.toString());

            buffer = new StringBuilder();
            buffer.append("║");
            for(int i = 0; i < maxSize; i++) {
                Die d = container.getDie(i);
                if(d == null){
                    buffer.append("    ");
                } else {
                    buffer.append(" ");
                    buffer.append(dieToString(d));
                    buffer.append(" ");
                }
                if(i != maxSize - 1) {
                    buffer.append("│");
                } else {
                    buffer.append("║");
                }
            }
            ret.add(buffer.toString());

            buffer = new StringBuilder();
            buffer.append("╚");
            for (int i = 0; i < maxSize - 1; i++) {
                buffer.append("════╧");
            }
            buffer.append("════╝");
            ret.add(buffer.toString());
            return ret.toArray(new String[0]);
        }

        private static String[] toStrings(Board board){
            return boardToStrings(board, false, null);
        }

        /**
         * Creates a String which represents a cell that can be printed on console.
         * @param cell object to read
         * @param printNumbers boolean to read
         * @param x coordinate to read
         * @param y coordinate to read
         * @param accepted check to read
         * @return "Stringfied" version of the cell.
         */
        private static String cellToString(Cell cell, boolean printNumbers, int x, int y, boolean accepted){
            StringBuilder buffer = new StringBuilder();
            if(cell.getRestriction() != null) {
                buffer.append(cell.getRestriction().toString());
            } else {
                buffer.append(" ");
            }
            if(printNumbers){
                if(accepted) {
                    buffer.append(SELECTION_MAP.charAt(x + y * 5));
                } else {
                    buffer.append(" ");
                }
            } else {
                buffer.append(" ");
            }
            if(cell.getDie() != null){
                buffer.append(dieToString(cell.getDie()));
            } else {
                buffer.append("  ");
            }
            return buffer.toString();
        }

        /**
         * Creates a String[] array which represents a given board that can be printed on console.
         * @param board Board to be printed
         * @param printSelectors If true, each cell will be printed with the corresponding selection letter
         * @param cellStates If `printSelectors` is true, this parameter can be used to print the cell selector only if `acceptedCell(cellStates)` is true. Set to null for no filtering. \
         *                   Performs the handling of the "first move situation" automatically (no filter on borders when `board.isEmpty()`).
         * @return "Stringified" version of the board.
         */
        private static String[] boardToStrings(Board board, boolean printSelectors, Set<CellState> cellStates){
            List<String> boardString = new ArrayList<>();
            boardString.add("┌────┬────┬────┬────┬────┐");
            for (int j = 0; j < board.getRows().size(); j++) {
                Cell[] row = board.getRow(j);
                StringBuilder buffer = new StringBuilder();
                for(int i = 0; i < row.length; i++){
                    buffer.append("│");
                    if(board.isEmpty()){
                        buffer.append(cellToString(row[i], printSelectors, i, j, i == 0 || i == row.length-1 || j == 0 || j == board.getRows().size()-1));
                    } else {
                        buffer.append(cellToString(row[i], printSelectors, i, j, acceptedCell(board, i, j, cellStates)));
                    }
                }
                buffer.append("│");
                boardString.add(buffer.toString());
                if(j != board.getRows().size() - 1) {
                    boardString.add("├────┼────┼────┼────┼────┤");
                }
            }
            boardString.add("└────┴────┴────┴────┴────┘");
            return boardString.toArray(new String[8]);
        }

        /**
         * Checks if the cell is accepted or not
         * @param diceContainer object to read
         * @param index int to read
         * @param cellStates FULL, EMPTY and/or NEAR
         * @return true if accepted, false otherwise
         */
        public static boolean acceptedCell(DiceContainer diceContainer, int index, Set<CellState> cellStates){
            if(cellStates == null)
                return true;

            if(cellStates.contains(CellState.FULL) && diceContainer.getDie(index) == null){
                return false;
            }
            if(cellStates.contains(CellState.EMPTY) && diceContainer.getDie(index) != null){
                return false;
            }
            return true;
        }

        /**
         * Checks if the cell is accepted or not
         * @param board object to read
         * @param x coordinate to read
         * @param y coordinate to read
         * @param cellStates FULL, EMPTY and/or NEAR
         * @return true if accepted, false otherwise
         */
        public static boolean acceptedCell(Board board, int x, int y, Set<CellState> cellStates) {
            if(cellStates == null){
                return true;
            }
            if(board.isEmpty()){
                return x == 0 || x == 4 || y == 0 || y == 3;
            }

            if (cellStates.contains(CellState.EMPTY) && !board.getCell(x, y).isEmpty()){
                return false;
            }
            if (cellStates.contains(CellState.FULL) && board.getCell(x, y).isEmpty()){
                return false;
            }
            if(cellStates.contains(CellState.NEAR) && board.getNeighbours(x, y).isEmpty()){
                return false;
            }
            if(cellStates.contains(CellState.NOT_NEAR) && !board.getNeighbours(x, y).isEmpty()){
                return false;
            }
            return true;
        }

        private static String[] display2(String[] a, String[] b){
            List<String> ret = new ArrayList<>();

            int paddingA = 0;
            if(a.length < b.length){
                paddingA = a[0].length();
            }
            int paddingB = b[0].length();

            int height = max(a.length, b.length);

            for (int i = 0; i < height; i++) {
                StringBuilder buffer = new StringBuilder();
                if(i < a.length){
                    buffer.append(a[i]);

                } else {
                    for (int j = 0; j < paddingA; j++) {
                        buffer.append(" ");
                    }
                }
                buffer.append("    ");
                if(i < b.length){
                    buffer.append(b[i]);
                } else {
                    for (int j = 0; j < paddingB; j++) {
                        buffer.append(" ");
                    }
                }
                ret.add(buffer.toString());
            }
            return ret.toArray(new String[0]);
        }

        /**
         * Creates a String  which represents a die that can be printed on console.
         * @param die object to read
         * @return "Stringfied" version of the die
         */
        private static String dieToString(Die die){
            StringBuilder buffer = new StringBuilder();
            int value = die.getValue();
            buffer.append(value);
            buffer.append(die.getColor());
            return buffer.toString();
        }

        /**
         * Shows a message based on CellState
         * @param cellStates EMPTY or FULL
         * @return string buffer
         */
        static String pickContainerMessage(Set<CellState> cellStates){
            StringBuilder buffer = new StringBuilder();
            buffer.append("Select a");
            if(cellStates.contains(CellState.EMPTY)) {
                buffer.append("n empty");
            } else if(cellStates.contains(CellState.FULL)){
                buffer.append(" non-empty");
            }
            buffer.append(" cell:");
            return buffer.toString();
        }

        public static String toString(Set<CellState> cellStates){
            StringBuilder buffer = new StringBuilder();
            buffer.append("elect a");
            if(cellStates.contains(CellState.EMPTY)) {
                buffer.append("n empty");
            } else if(cellStates.contains(CellState.FULL)){
                buffer.append(" non-empty");
            }
            buffer.append(" cell");
            if(cellStates.contains(CellState.NEAR)){
                buffer.append(" that is near a die");
            } else if (cellStates.contains(CellState.NOT_NEAR)) {
                buffer.append(" that is not near a die");
            }
            buffer.append(":");
            return buffer.toString();
        }

        /**
         * Creates a String[] array that represent the score to print on console
         * @param player object to read
         * @param score object to read
         * @return "Stringfied" version of the scores
         */
        private static String[] scoreToStrings(Player player, Score score) {
            List<String> ret = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            int[] points = score.getValues();
            boolean winner = player.isWinner();
            boolean disconnected = player.isDisconnected();
            buffer.append("┏");
            for(int i = 0; i < PLAYER_WIDTH; i++)
                buffer.append("━");
            buffer.append("┓");
            ret.add(buffer.toString());

            ret.add(scoreLine(player.getName() + (disconnected ? " (DISCONN.)" : ""), -1, " ", false));
            ret.add(scoreLine("Private Card", points[0], "   ", true));
            ret.add(scoreLine("Public Cards", points[1], "   ", true));
            ret.add(scoreLine("Tokens", points[2], "   ", true));
            ret.add(scoreLine("Empty cells", points[3], "   ", true));
            ret.add(scoreLine("", -1, "", false));
            ret.add(scoreLine("Total Score", score.getOverallScore(), "   ", true));
            ret.add(scoreLine("", -1, "", false));
            ret.add(scoreLine(winner ? "(WINNER)" : "", -1, "                  ", false));

            buffer = new StringBuilder();
            buffer.append("┗");
            for(int i = 0; i < PLAYER_WIDTH; i++)
                buffer.append("━");
            buffer.append("┛");
            ret.add(buffer.toString());

            return ret.toArray(new String[0]);
        }

        /**
         * Create a line (String) for score displaying.
         * @param name Name of the parameter
         * @param value Points of the related parameter. Values below zero will not be printed.
         * @param leftPadding String of spaces that will be inserted at the end of the line, right after `┃`.
         * @return String defined with parameters, with total length == `PLAYER_WIDTH` and `┃` characters at the start and at the end of the line.
         */
        private static String scoreLine(String name, int value, String leftPadding, boolean showValue){
            StringBuilder buffer = new StringBuilder();
            buffer.append("┃");
            buffer.append(leftPadding);
            buffer.append(name);
            for(int i = buffer.length() - 1; i < 22; i++) {
                buffer.append(" ");
            }
            if(showValue) {
                buffer.append(value);
            }
            for(int i = buffer.length() - 1; i < PLAYER_WIDTH; i++){
                buffer.append(" ");
            }
            buffer.append("┃");

            return buffer.toString();
        }
    }
}