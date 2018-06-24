package it.polimi.se2018.view;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.*;
import it.polimi.se2018.network.message.PatternRequest;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.max;

/**
 * @author MicheleLambertucci
 */
public class CLI implements ViewInterface {
    private PlayerState state;
    private Match match;
    private Client client;

    private static final int PLAYER_HEIGHT = 11;
    private static final int PLAYER_WIDTH = 47;
    private static final String selectionMap = "ABCDEFGHIJKLMNOPQRST";
    private static final int CARD_WIDTH = 26;

    public CLI(Client client){
        this.client = client;
    }

    public void launch(){
        askLogin();
        askTypeOfConnection();
        try {
            client.connect();
        } catch (Exception e) {
            client.onConnectionError(e);
        }
    }

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

    private void askTypeOfConnection(){
        List<String> connections = new ArrayList<>();
        connections.add("Socket");
        connections.add("RMI");

        int selected = makeSelection(connections);
        if(selected == 0){
            client.setConnection(false);
        } else {
            client.setConnection(true);
        }
    }

    @Override
    public void onMatchStart(Match match) {
        this.match = match;
        displayMatch(match);
        if(getMyself(match).getState().get() == EnumState.YOUR_TURN){
            onYourTurnState();
        }
    }

    @Override
    public void onPatternRequest(PatternRequest message) {
        List<Integer> selections = new ArrayList<>();

        int iteration = 0;
        for(Board b : message.boards){
            System.out.println(iteration);
            printLines(Stringifier.toStrings(b));

            selections.add(iteration);

            iteration++;
        }

        int selection = makeSelection(selections);
        client.sendPatternResponse(selection);
    }

    private void displayMatch(Match match){
        System.out.println("+ ROUND TRACKER +                                      + DRAFT POOL +");
        printLines(Stringifier.display2(Stringifier.diceContainerToStrings(match.getRoundTracker(), false, null), Stringifier.diceContainerToStrings(match.getDraftPool(), false, null)));
        displayPlayers(match.getPlayers().toArray(new Player[0]));
        System.out.println("+ TOOLCARDS +");
        displayCards(match.getToolCards());
        System.out.println("+ PUBLIC OBJECTIVE CARDS +");
        displayCards(match.getPublicObjCards());
    }

    private static void displayCards(Card[] cards){
        if(cards.length != 3)
            throw new RuntimeException("cards.length must be 3");
        printLines(Stringifier.display2(Stringifier.display2(Stringifier.toStrings(cards[0]), Stringifier.toStrings(cards[1])), Stringifier.toStrings(cards[2])));
    }

    private static void printLines(String[] lines){
        for (String line : lines) {
            System.out.println(line);
        }
    }

    private void displayPlayers(Player[] players){
        int rows = players.length / 2;
        for (int i = 0; i < rows; i++) {
            printLines(Stringifier.display2(toStrings(players[i]), toStrings(players[i+1])));
        }
        if(players.length % 2 == 1){
            printLines(toStrings(players[players.length - 1]));
        }
    }

    private String[] toStrings(Player player){
        String[] boardString = Stringifier.toStrings(player.getBoard());

        int tokens = player.getToken();
        List<String> ret = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < tokens; i++){
            buffer.append("*");
        }
        String tokenString = buffer.toString();

        String[] firstCol = new String[]{
                "Player:",
                "" + player.getName(),
                player.getName().equals(client.getUsername()) ? "  (YOU)" : "",
                player.getState().get().toString(),
                "",
                "Tokens:",
                " " + tokenString,
                player.getName().equals(client.getUsername()) ? player.getPrivateObjCard().getColor().toString() : ""
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

    private void onChangeState(PlayerState oldState, PlayerState newState){
        ClientMove move = null;
        switch(newState.get()){
            case IDLE:
                break;
            case PICK:
                move = onPickState(newState);
                break;
            case VALUE:
                move = onValueState(newState);
                break;
            case UPDOWN:
                move = onUpDownState(newState);
                break;
            case YESNO:
                move = onYesNoState(newState);
                break;
            case REPEAT:
                onRepeatState(oldState);
                return;
            case YOUR_TURN:
                onYourTurnState();
                return;
        }
        if(move != null){
            client.sendMove(move);
        }
    }


    private void onYourTurnState() {
        System.out.println("    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("    ┃              = TOCCA A TE =            ┃");
        System.out.println("    ┠────────────────────────────────────────┨");
        System.out.println("    ┃  Cosa vuoi fare?                       ┃");
        System.out.println("    ┃  0 - Prendi un dado dalla Draft Pool   ┃");
        System.out.println("    ┃  1 - Usa una ToolCard                  ┃");
        System.out.println("    ┃  2 - Passa                             ┃");
        System.out.println("    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");

        Scanner sc = new Scanner(System.in);
        int selection = -1;
        while(selection == -1){
            if(sc.hasNextInt()){
                selection = sc.nextInt();
                if(selection < 0 || selection >= 3){
                    selection = -1;
                    System.out.println("Type a number between 0 and 2");
                }
            } else {
                sc.next();
                selection = -1;
                System.out.println("Type a number between 0 and 2");
            }
        }

        switch(selection){
            case 0:
                client.activateNormalMove();
                break;
            case 1:
                int index = askToolCardActivation();
                client.activateToolCard(index);
                break;
            case 2:
                client.pass();
                break;
            default:
                throw new UnsupportedOperationException();
        }
    }

    private int askToolCardActivation() {
        displayCards(match.getToolCards());
        List<String> selectables = new ArrayList<>();
        Arrays.stream(match.getToolCards()).map(ToolCard::getTitle).forEachOrdered(selectables::add);
        return makeSelection(selectables);
    }

    private void onRepeatState(PlayerState oldState) {
        System.out.println("Mossa non valida! Riprova.");
        setState(oldState);
    }

    private ClientMove onUpDownState(PlayerState newState) {
        System.out.println("Do you want to add or subtract 1 to the die value?");
        List<String> selections = Arrays.stream(new String[]{"+1", "-1"}).collect(Collectors.toList());
        int selection = makeSelection(selections);
        return new UpDownMove(selection == 0);
    }

    private ClientMove onYesNoState(PlayerState newState) {
        System.out.println("Do you want to continue?");
        List<String> selections = Arrays.stream(new String[]{"Yes", "No"}).collect(Collectors.toList());
        int selection = makeSelection(selections);
        return new YesNoMove(selection == 0);
    }

    private ClientMove onValueState(PlayerState newState) {
        System.out.println("Now pick a value from 1 to 6");
        Scanner sc = new Scanner(System.in);
        int selected = -1;
        while(selected == -1){
            if(sc.hasNextInt()){
                selected = sc.nextInt();
                if(selected < 1 || selected > 6){
                    selected = -1;
                    System.out.println("Unacceptable value. Pick a value from 1 to 6");
                }
            } else {
                sc.next();
                System.out.println("Unacceptable value. Pick a value from 1 to 6");
            }
        }
        return new ValueMove(selected);
    }

    private ClientMove onPickState(PlayerState newState){
        PickState pState= (PickState) newState;
        printPick(pState);
        System.out.println("From which container do you want to pick a cell?");

        List<Component> actives = new ArrayList<>();
        actives.addAll(pState.getActiveContainers());

        ClientMove move = null;

        int selection = makeSelection(actives);
        switch(actives.get(selection)){
            case BOARD:
                move = pickBoard(getMyself(match).getBoard(), pState.getCellStates());
                break;
            case DRAFTPOOL:
                move = pickDiceContainer(match.getDraftPool(), pState.getCellStates(), true);
                break;
            case ROUNDTRACKER:
                move = pickDiceContainer(match.getRoundTracker(), pState.getCellStates(), false);
                break;
            default:
                 // do nothing
        }

        return move;
    }

    private ClientMove pickDiceContainer(DiceContainer diceContainer, EnumSet<CellState> cellStates, boolean isDraftPool) {
        printLines(Stringifier.diceContainerToStrings(diceContainer, true, cellStates));
        System.out.println(Stringifier.pickContainerMessage(cellStates));
        Scanner sc = new Scanner(System.in);
        Pattern ptn = Pattern.compile("[A-T]?", Pattern.CASE_INSENSITIVE);
        int selection = -1;
        while(selection == -1){
            if(sc.hasNext(ptn)){
                String found = sc.next(ptn);
                selection = selectionMap.indexOf(found.toUpperCase().charAt(0));
                if(selection < 0 || selection >= diceContainer.getMaxSize() || !Stringifier.acceptedCell(diceContainer, selection, cellStates)){
                    System.out.println("Unacceptable selection. " + Stringifier.pickContainerMessage(cellStates));
                    selection = -1;
                }
            }  else {
                sc.next();
                System.out.println("Unacceptable selection. " + Stringifier.pickContainerMessage(cellStates));
            }
        }
        return new DiceContainerCoordMove(selection, isDraftPool ? DiceContainerCoordMove.DiceContainerName.DRAFT_POOL : DiceContainerCoordMove.DiceContainerName.ROUND_TRACKER);
    }

    private ClientMove pickBoard(Board board, EnumSet<CellState> cellStates) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("Now s");
        buffer.append(Stringifier.toString(cellStates));
        buffer.append(" (type the corresponding character)");
        System.out.println(buffer.toString());
        printLines(Stringifier.boardToStrings(board, true, cellStates));
        Scanner sc = new Scanner(System.in);
        int selection = -1;
        Pattern ptn = Pattern.compile("[A-T]?", Pattern.CASE_INSENSITIVE);
        while(selection == -1){
            if(sc.hasNext(ptn)) {
                String found = sc.next(ptn);
                selection = selectionMap.indexOf(found.toUpperCase().charAt(0));
                if(!(Stringifier.acceptedCell(board, selection % 5, selection / 5, cellStates) ||
                        (board.isEmpty() &&
                                (selection % 5 == 0 || selection % 5 == board.getRow(0).length-1
                                        || selection / 5 == 0 || selection / 5 == board.getRows().size()-1)))){
                    buffer = new StringBuilder();
                    buffer.append("The cell you selected is not acceptable. S");
                    buffer.append(Stringifier.toString(cellStates));
                    buffer.append(" (type the corresponding character)");
                    System.out.println(buffer.toString());
                    selection = -1;
                }
            } else {
                buffer = new StringBuilder();
                buffer.append("The cell you selected is not acceptable. S");
                buffer.append(Stringifier.toString(cellStates));
                buffer.append(" (type the corresponding character)");
                System.out.println(buffer.toString());
                sc.next();
                selection = -1;
            }
        }
        int x = selection % 5;
        int y = selection / 5;

        return new BoardCoordMove(x, y);
    }

    private int makeSelection(List selectables){
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < selectables.size(); i++){
            buffer.append(i);
            buffer.append(": ");
            buffer.append(selectables.get(i).toString());
            buffer.append("    ");
        }
        System.out.println(buffer.toString());
        System.out.println("Select one option");
        Scanner sc = new Scanner(System.in);
        int i;
        if(sc.hasNextInt()) {
            i = sc.nextInt();
        } else {
            sc.next();
            i = -1;
        }
        while(i < 0 || i >= selectables.size()){
            System.out.println("Type a number between 0 and " + (selectables.size() - 1));
            if(sc.hasNextInt()){
                i = sc.nextInt();
            } else {
                sc.next();
                i = -1;
            }
        }
        return i;
    }

    private void printPick(PickState pState){
        EnumSet<Component> containers = pState.getActiveContainers();
        EnumSet<CellState> states = pState.getCellStates();
        String[] columnBoard = null;
        if(containers.contains(Component.BOARD)){
            columnBoard = toStrings(getMyself(match));
        }

        String[] roundTracker = null;
        if(containers.contains(Component.ROUNDTRACKER)){
            String title = "+ ROUND TRACKER +";
            roundTracker = Stream.concat(Arrays.stream(new String[]{title}), Arrays.stream(Stringifier.diceContainerToStrings(match.getRoundTracker(), false, null))).toArray(String[]::new);
        }

        String[] draftPool = null;
        if(containers.contains(Component.DRAFTPOOL)){
            String title = "+ DRAFT POOL +";
            draftPool = Stream.concat(Arrays.stream(new String[]{title}), Arrays.stream(Stringifier.diceContainerToStrings(match.getDraftPool(), false, null))).toArray(String[]::new);
        }

        String[] col2 = null;
        if (draftPool != null && roundTracker != null) {
            col2 = Stream.of(Arrays.stream(roundTracker), Arrays.stream(new String[]{""}), Arrays.stream(draftPool))
                    .flatMap(s -> s)
                    .toArray(String[]::new);
        } else if(draftPool != null){
            col2 = draftPool;
        } else if(roundTracker != null){
            col2 = roundTracker;
        }

        if(columnBoard != null && col2 != null) {
            printLines(Stringifier.display2(columnBoard, col2));
        } else if(columnBoard != null){
            printLines(columnBoard);
        } else if(col2 != null){
            printLines(col2);
        }
    }

    private Player getMyself(Match m){
        return m.getPlayers().stream()
                .filter(p -> p.getName().equals(client.getUsername()))
                .findFirst().orElse(null);
    }

    /**
     * What to do when logged in successful?
     * Print a "waiting" message
     */
    public void onConnect(){
        System.out.println("Login successful.");
        System.out.println("Press ENTER to enter the queue.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        client.sendQueueRequest();
    }

    public void onNewMatchState(Match oldMatch, Match newMatch) {
        if(newMatch.isFinished()){
            displayScores(newMatch);
        } else {
            displayMatch(newMatch);
            Player me = null;
            for (Player p : newMatch.getPlayers()) {
                if (p.getName().equals(client.getUsername()))
                    me = p;
            }
            if (me != null) {
                setState(me.getState());
            }
        }
    }

    private void displayScores(Match newMatch) {
        for(Player p : newMatch.getPlayers()) {
            printLines(Stringifier.scoreToStrings(p, newMatch.getScore(p)));
        }
    }

    public void onToolCardActivationResponse(boolean isOk){
        if(isOk) {
            System.out.println("Toolcard attivata");
        } else {
            System.out.println("Non sono disponibili abbastanza token per attivare la Toolcard");
        }
    }

    public void setState(PlayerState state) {
        PlayerState oldState = this.state;
        this.state = state;
        onChangeState(oldState, state);
    }

    public void updateMatch(Match newMatch) {
        Match oldMatch = match;
        match = newMatch;
        onNewMatchState(oldMatch, match);
    }

    @Override
    public void onConnectionError(Exception e) {
        System.out.println("    ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("    ┃         ERRORE DI CONNESSIONE       ┃");
        System.out.println("    ┠─────────────────────────────────────┨");
        System.out.println("    ┃  Si è  verificato  un  errore  di   ┃");
        System.out.println("    ┃  connessione.  L'applicazione  si   ┃");
        System.out.println("    ┃  spegnerà.                          ┃");
        System.out.println("    ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
        e.printStackTrace();
    }

    private static class Stringifier{
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

        private static String[] diceContainerToStrings(DiceContainer container, boolean printSelectors, EnumSet<CellState> cellStates){
            List<String> ret = new ArrayList<>();

            int maxSize = container.getMaxSize();
            StringBuilder buffer = new StringBuilder();
            buffer.append("╔");
            if(printSelectors){
                // print selection character for each row accepted by cellState
                for(int i = 0; i < maxSize - 1; i++){
                    if(acceptedCell(container, i, cellStates)) {
                        buffer.append("══");
                        buffer.append(selectionMap.charAt(i));
                        buffer.append("═╤");
                    } else {
                        buffer.append("════╤");
                    }
                }

                // print last cell
                if(acceptedCell(container, maxSize - 1, cellStates)) {
                    buffer.append("══");
                    buffer.append(selectionMap.charAt(maxSize - 1));
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

        private static String cellToString(Cell cell, boolean printNumbers, int x, int y, boolean accepted){
            StringBuilder buffer = new StringBuilder();
            if(cell.getRestriction() != null) {
                buffer.append(cell.getRestriction().toString());
            } else {
                buffer.append(" ");
            }
            if(printNumbers){
                if(accepted) {
                    buffer.append(selectionMap.charAt(x + y * 5));
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
        private static String[] boardToStrings(Board board, boolean printSelectors, EnumSet<CellState> cellStates){
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

        private static boolean acceptedCell(DiceContainer diceContainer, int index, EnumSet<CellState> cellStates){
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

        private static boolean acceptedCell(Board board, int x, int y, EnumSet<CellState> cellStates) {
            if(cellStates == null){
                return true;
            }
            if (cellStates.contains(CellState.EMPTY)) {
                if(!board.getCell(x, y).isEmpty())
                    return false;
            }
            if (cellStates.contains(CellState.FULL)) {
                if(board.getCell(x, y).isEmpty()){
                    return false;
                }
            }
            if(cellStates.contains(CellState.NEAR)){
                if(board.getNeighbours(x, y).isEmpty()){
                    return false;
                }
            }
            if(cellStates.contains(CellState.NOTNEAR)){
                if(!board.getNeighbours(x, y).isEmpty()){
                    return false;
                }
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

        private static String dieToString(Die die){
            StringBuilder buffer = new StringBuilder();
            int value = die.getValue();
            buffer.append(value);
            buffer.append(die.getColor());
            return buffer.toString();
        }

        private static String pickContainerMessage(EnumSet<CellState> cellStates){
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

        private static String toString(EnumSet<CellState> cellStates){
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
            } else if (cellStates.contains(CellState.NOTNEAR)) {
                buffer.append(" that is not near a die");
            }
            buffer.append(":");
            return buffer.toString();
        }

        private static String[] scoreToStrings(Player player, Score score) {
            List<String> ret = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            int[] points = score.getValues();
            buffer.append("┏");
            for(int i = 0; i < PLAYER_WIDTH; i++)
                buffer.append("━");
            buffer.append("┓");
            ret.add(buffer.toString());

            ret.add(scoreLine(player.getName(), -1, " "));
            ret.add(scoreLine("Private Card", points[0], "   "));
            ret.add(scoreLine("Public Cards", points[1], "   "));
            ret.add(scoreLine("Tokens", points[2], "   "));
            ret.add(scoreLine("Empty cells", points[3], "   "));
            ret.add(scoreLine("", -1, ""));
            ret.add(scoreLine("Total Score", score.getOverallScore(), "   "));
            ret.add(scoreLine("", -1, ""));

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
        private static String scoreLine(String name, int value, String leftPadding){
            StringBuilder buffer = new StringBuilder();
            buffer.append("┃");
            buffer.append(leftPadding);
            buffer.append(name);
            for(int i = buffer.length() - 1; i < 22; i++) {
                buffer.append(" ");
            }
            if(value >= 0) {
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
