package it.polimi.se2018.model;

import java.util.Observable;
import java.util.Observer;

/**
 * This is the class that contains the board and the coordinates
 * @version 1.0
 */
public class BoardCoord implements Observer {

    private Board board;
    private int x;
    private int y;

    public BoardCoord(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
    }

    @Override
    public void update(Observable o, Object arg) {
        // TODO
    }
}
