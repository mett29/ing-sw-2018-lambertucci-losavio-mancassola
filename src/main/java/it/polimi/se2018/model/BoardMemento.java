package it.polimi.se2018.model;

/**
 * This class represents the saved state of the Board object
 * Memento Pattern
 * @version 1.0
 */
public class BoardMemento {

    private Cell[][] window;
    private static final int boardWidth = 5;
    private static final int boardHeight = 4;

    public BoardMemento(Cell[][] savedWindow) {
        this.window = savedWindow;
    }

    public Cell[][] getWindow() {
        return this.window;
    }

}
