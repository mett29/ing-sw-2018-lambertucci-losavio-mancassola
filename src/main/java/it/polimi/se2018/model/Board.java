package it.polimi.se2018.model;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * This class represent the player's board
 * @version 1.0, 02/05/2018
 */
public class Board implements Iterable<Cell> {

    private Cell[][] window;
    private static final int boardWidth = 5;
    private static final int boardHeight = 4;

    public Board() {
        this.window = new Cell[4][5];
        reset();
    }

    /**
     * @param x first coordinate of the Die to check
     * @param y second coordinate of the Die to check
     * @param die the Die to check
     * @return the related error
     */
    public PlacementError isDieAllowed(int x, int y, Die die) {

        PlacementError ret = new PlacementError();

        // Check if the placed Die violate the restriction
        ret = PlacementError.union(ret, window[x][y].isDieAllowed(die));

        List<Die> adjacentDies = getNeighbours(x, y);

        // Check that all the adjacent dies don't have the same color or the same value of the placed die
        for (Die neighbour : adjacentDies) {
            if (neighbour.getColor() == die.getColor())
                ret = PlacementError.union(ret, new PlacementError(Flags.COLOR));
            if (neighbour.getValue() == die.getValue())
                ret = PlacementError.union(ret, new PlacementError(Flags.VALUE));
        }

        // Check if the Die is near other dices
        if (getNeighbours(x, y).isEmpty())
            ret = PlacementError.union(ret, new PlacementError(Flags.NEIGHBOURS));

        // Check if the Die has been placed on an edge
        if (countDices() == 0)
            if (x != 0 && y != 0 && x != 4 && y != 3)
                ret = PlacementError.union(ret, new PlacementError(Flags.EDGE));

        return ret;
    }

    /**
     * @return the Die in a specific position on the board
     */
    public Die getDie(int x, int y) {
        if (x < 0 || y < 0 || x > 4 || y > 3)
            throw new InvalidParameterException();
        return window[x][y].getDie();
    }

    /**
     * @param x first coordinate of the Die object
     * @param y second coordinate of the Die object
     * @param die the Die object to set in the player's board
     */
    public void setDie(int x, int y, Die die) {
        if (x < 0 || y < 0 || x > 4 || y > 3)
            throw new InvalidParameterException();
        window[x][y].setDie(die);
    }

    /**
     * This method returns the 8 Dies that are adjacent to a specific cell
     * @param x first coordinate of the Die
     * @param y second coordinate of the Die
     * @return Die[] containing the 8 adjacent dies
     */
    private List<Die> getNeighbours(int x, int y) {
        List<Die> adjacentDies = new ArrayList<>();

        int leftX = (x - 1 + boardWidth) % boardWidth;
        int rightX = (x + 1) % boardWidth;
        int aboveY = (y - 1 + boardHeight) % boardHeight;
        int belowY = (y + 1) % boardHeight;

        adjacentDies.add(window[leftX][y].getDie());
        adjacentDies.add(window[x][belowY].getDie());
        adjacentDies.add(window[rightX][y].getDie());
        adjacentDies.add(window[x][aboveY].getDie());

        return adjacentDies;
    }

    /**
     * @param index index of the column we want the method to return
     * @return the index(th) column
     */
    public Cell[] getColumn(int index) {
        Cell[] col = new Cell[5];
        for(int row = 0; row < 4; row++)
            col[row] = window[row][index];
        return col;
    }

    /**
     * @param index index of the row we want the method to return
     * @return the index(th) row
     */
    public Cell[] getRow(int index) {
        return window[index];
    }

    /**
     * This method returns all the columns of the board
     * @return cols, an ArrayList containing all the board's columns
     */
    public List<Cell[]> getColumns() {
        List<Cell[]> cols = new ArrayList<>();

        for (int i = 0; i < boardWidth; i++) {
            cols.add(getColumn(i));
        }
        return cols;
    }

    /**
     * This method returns all the rows of the board
     * @return rows, an ArrayList containing all the board's rows
     */
    public List<Cell[]> getRows() {
        List<Cell[]> rows = new ArrayList<>();

        for (int i = 0; i < boardHeight; i++) {
            rows.add(getRow(i));
        }
        return rows;
    }

    /**
     * This method reset the board
     */
    private void reset() {
        for (Cell[] cells : window)
            for (Cell cell : cells) cell.setDie(new Die());
    }

    public int countDices() {
        int counter = 0;
        for (Cell[] cells : window)
            for (Cell cell : cells)
                if (!cell.isEmpty())
                    counter++;
        return counter;
    }

    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<Cell>() {
            private int index = 0;

            private int getX() {
                return index % 5;
            }

            private int getY() {
                return index / 5; // integer division
            }

            @Override
            public boolean hasNext() {
                return getX() < 5 && getY() < 4;
            }

            @Override
            public Cell next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                index++;
                return window[getX()][getY()];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
