package it.polimi.se2018.view.CLI;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.CellState;
import it.polimi.se2018.model.DiceContainer;
import it.polimi.se2018.network.client.BoardCoordMove;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.client.DiceContainerCoordMove;

import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class InputManager {
    private InputManager(){}

    private Thread thread;

    public static final String selectionMap = "ABCDEFGHIJKLMNOPQRST";

    private static InputManager instance;
    private static InputManager getInstance(){
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }

    public static void ask(Pattern accepted, Function<String, Integer> converter, Consumer<Integer> onSelected){
        getInstance().thread = new Thread(() -> {
            int selected = -1;
            Scanner scanner = new Scanner(System.in);
            while(selected ==-1){
                if(Thread.currentThread().isInterrupted()){
                    return;
                }
                if (scanner.hasNext(accepted)) {
                    String input = scanner.next(accepted);
                    selected = converter.apply(input);
                    onSelected.accept(selected);
                } else {
                    scanner.next();
                    selected = -1;
                }
            }
        });

        getInstance().thread.start();
    }

    public static void askBoard(Board board, EnumSet<CellState> cellStates, Client client){
        getInstance().thread = new Thread(() -> {
            Pattern ptn = Pattern.compile("[A-T]?", Pattern.CASE_INSENSITIVE);
            Scanner sc = new Scanner(System.in);
            int selection = -1;
            while(selection == -1){
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                if(sc.hasNext("undo")){
                    client.sendUndoRequest();
                }
                if(sc.hasNext(ptn)) {
                    String found = sc.next(ptn);
                    selection = selectionMap.indexOf(found.toUpperCase().charAt(0));
                    if(!(CLI.Stringifier.acceptedCell(board, selection % 5, selection / 5, cellStates))){
                        String message = "The cell you selected is not acceptable. S" +
                                CLI.Stringifier.toString(cellStates) +
                                " (type the corresponding character)";
                        System.out.println(message);
                        selection = -1;
                    }
                } else {
                    StringBuilder buffer = new StringBuilder();
                    buffer.append("The cell you selected is not acceptable. S");
                    buffer.append(CLI.Stringifier.toString(cellStates));
                    buffer.append(" (type the corresponding character)");
                    System.out.println(buffer.toString());
                    sc.next();
                    selection = -1;
                }
            }
            int x = selection % 5;
            int y = selection / 5;

            client.sendMove(new BoardCoordMove(x, y));
        });

        getInstance().thread.start();
    }

    public static void askDiceContainer(DiceContainer diceContainer, EnumSet<CellState> cellStates, boolean isDraftPool, Client client){
        getInstance().thread = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            Pattern ptn = Pattern.compile("[A-T]?", Pattern.CASE_INSENSITIVE);
            int selection = -1;
            while(selection == -1){
                if(Thread.currentThread().isInterrupted()){
                    return;
                }
                if(sc.hasNext("undo")){
                    client.sendUndoRequest();
                }
                if(sc.hasNext(ptn)){
                    String found = sc.next(ptn);
                    selection = selectionMap.indexOf(found.toUpperCase().charAt(0));
                    if(selection < 0 || selection >= diceContainer.getMaxSize() || !CLI.Stringifier.acceptedCell(diceContainer, selection, cellStates)){
                        System.out.println("Unacceptable selection. " + CLI.Stringifier.pickContainerMessage(cellStates));
                        selection = -1;
                    }
                }  else {
                    sc.next();
                    System.out.println("Unacceptable selection. " + CLI.Stringifier.pickContainerMessage(cellStates));
                }
            }
            client.sendMove(new DiceContainerCoordMove(selection,
                    isDraftPool ? DiceContainerCoordMove.DiceContainerName.DRAFT_POOL : DiceContainerCoordMove.DiceContainerName.ROUND_TRACKER));
        });
        getInstance().thread.start();
    }

    public static void ask(List selectables, Consumer<Integer> onSelected){
        getInstance().thread = new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            int i = -1;
            while(i < 0 || i >= selectables.size()){
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                System.out.println("Type a number between 0 and " + (selectables.size() - 1));
                if(sc.hasNextInt()){
                    i = sc.nextInt();
                } else {
                    sc.next();
                    i = -1;
                }
            }
            onSelected.accept(i);
        });

        getInstance().thread.start();
    }

    public static void closeInput(){
        try {
            getInstance().thread.interrupt();
        } catch(NullPointerException e){
            // do nothing
        }
    }
}
