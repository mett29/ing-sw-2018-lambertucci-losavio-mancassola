package it.polimi.se2018.view.cli;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.CellState;
import it.polimi.se2018.model.DiceContainer;
import it.polimi.se2018.network.client.BoardCoordMove;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.client.DiceContainerCoordMove;
import it.polimi.se2018.view.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * This class is used to separate the logic of the CLI from the input listener
 * @author MicheleLambertucci
 */
class InputManager {
    private InputManager(){}

    private Thread thread;

    private static PrintStream ps = new PrintStream(System.out);

    static final String SELECTION_MAP = "ABCDEFGHIJKLMNOPQRST";

    private static Logger logger = Logger.getLogger("inputManager");

    private static InputManager instance;
    private static InputManager getInstance(){
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    static void ask(Pattern accepted, Function<String, Integer> converter, Consumer<Integer> onSelected){
        getInstance().thread = new Thread(() -> {
            int selected = -1;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            Scanner scanner = new Scanner(bufferedReader);
            try {
                while (selected == -1) {
                    while (!bufferedReader.ready()) {
                        Thread.sleep(100);
                    }
                    if (scanner.hasNext(accepted)) {
                        String input = scanner.next(accepted);
                        selected = converter.apply(input);
                        onSelected.accept(selected);
                    } else {
                        scanner.next();
                        selected = -1;
                        ps.println("Unacceptable selection, repeat again");
                    }
                }
            } catch(IOException|InterruptedException e){
                // do nothing: thread closes
            }
        });

        getInstance().thread.start();
    }

    static void askBoard(Board board, Set<CellState> cellStates, Client client){
        getInstance().thread = new Thread(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            Pattern ptn = Pattern.compile("[A-T]?", Pattern.CASE_INSENSITIVE);
            Scanner sc = new Scanner(bufferedReader);
            int selection = -1;
            try {
                while (selection == -1) {
                    while (!bufferedReader.ready()) {
                        Thread.sleep(100);
                    }
                    if (sc.hasNext("undo")) {
                        client.sendUndoRequest();
                    }
                    if (sc.hasNext(ptn)) {
                        String found = sc.next(ptn);
                        selection = SELECTION_MAP.indexOf(found.toUpperCase().charAt(0));
                        if (!(Utils.acceptedCell(board, selection % 5, selection / 5, cellStates))) {
                            String message = "The cell you selected is not acceptable. S" +
                                    CLI.Stringifier.toString(cellStates) +
                                    " (type the corresponding character)";
                            ps.println(message);
                            selection = -1;
                        }
                    } else {
                        StringBuilder buffer = new StringBuilder();
                        buffer.append("The cell you selected is not acceptable. S");
                        buffer.append(CLI.Stringifier.toString(cellStates));
                        buffer.append(" (type the corresponding character)");
                        ps.println(buffer.toString());
                        sc.next();
                        selection = -1;
                    }
                }
                int x = selection % 5;
                int y = selection / 5;

                client.sendMove(new BoardCoordMove(x, y));
            } catch(IOException|InterruptedException e){
                // do nothing: thread closes
            }
        });

        getInstance().thread.start();
    }

    static void askDiceContainer(DiceContainer diceContainer, Set<CellState> cellStates, boolean isDraftPool, Client client){
        getInstance().thread = new Thread(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            Scanner sc = new Scanner(bufferedReader);
            Pattern ptn = Pattern.compile("[A-T]?", Pattern.CASE_INSENSITIVE);
            int selection = -1;
            try {
                while (selection == -1) {
                    while (!bufferedReader.ready()) {
                        Thread.sleep(100);
                    }
                    if (sc.hasNext("undo")) {
                        client.sendUndoRequest();
                    }
                    if (sc.hasNext(ptn)) {
                        String found = sc.next(ptn);
                        selection = SELECTION_MAP.indexOf(found.toUpperCase().charAt(0));
                        if (selection < 0 || selection >= diceContainer.getMaxSize() || !Utils.acceptedCell(diceContainer, selection, cellStates)) {
                            ps.println("Unacceptable selection. " + CLI.Stringifier.pickContainerMessage(cellStates));
                            selection = -1;
                        }
                    } else {
                        sc.next();
                        ps.println("Unacceptable selection. " + CLI.Stringifier.pickContainerMessage(cellStates));
                    }
                }
                client.sendMove(new DiceContainerCoordMove(selection,
                        isDraftPool ? DiceContainerCoordMove.DiceContainerName.DRAFT_POOL : DiceContainerCoordMove.DiceContainerName.ROUND_TRACKER));
            } catch(IOException|InterruptedException e){
                // do nothing: thread closes
            }
        });
        getInstance().thread.start();
    }

    static void ask(List selectables, Consumer<Integer> onSelected){
        getInstance().thread = new Thread(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            Scanner sc = new Scanner(bufferedReader);
            int i = -1;
            try {
                while (i < 0 || i >= selectables.size()) {
                    ps.println("Type a number between 0 and " + (selectables.size() - 1));
                    while (!bufferedReader.ready()) {
                        Thread.sleep(100);
                    }
                    if (sc.hasNextInt()) {
                        i = sc.nextInt();
                    } else {
                        sc.next();
                        i = -1;
                    }
                }
                onSelected.accept(i);
            } catch(IOException|InterruptedException e){
                // do nothing: thread closes
            }
        });

        getInstance().thread.start();
    }

    static void closeInput(){
        try {
            // If there is something in System.in, consume it
            BufferedReader tmp = new BufferedReader(new InputStreamReader(System.in));
            if (tmp.ready()) {
                tmp.readLine();
            }
            getInstance().thread.interrupt();
        } catch(NullPointerException|IOException e){
            logger.log(Level.WARNING,e.getMessage());
        }
    }
}
