package it.polimi.se2018.model;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * This class represent the player's board
 * @author mett29, MicheleLambertucci
 * @version 1.1
 */
public class Board implements Iterable<Cell>, Memento<Board> {

    private Cell[][] window;
    private static final int boardWidth = 5;
    private static final int boardHeight = 4;
    private int boardDifficulty;

    /**
     * Create board with `pattern` window frame
     * @param pattern Matrix of restrictions
     * @param boardDifficulty Number of token to be assigned to the player who chooses this board
     */
    public Board(Restriction[][] pattern, int boardDifficulty){
        this.window = new Cell[boardHeight][boardWidth];

        if(boardDifficulty <= 0){
            throw new InvalidParameterException("Board Difficulty must be greater than 0");
        }

        this.boardDifficulty = boardDifficulty;

        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                window[i][j] = new Cell(pattern[i][j]);
            }
        }
    }

    /**
     * Copy constructor
     * @param board object to deep copy
     */
    public Board(Board board){
        this.window = new Cell[boardHeight][boardWidth];
        this.boardDifficulty = board.getBoardDifficulty();
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                this.window[i][j] = new Cell(board.window[i][j].getRestriction());
                if(board.window[i][j].getDie() != null) {
                    this.window[i][j].setDie(new Die(board.window[i][j].getDie()));
                }
            }
        }
    }

    /**
     * @param x first coordinate of the Die to check
     * @param y second coordinate of the Die to check
     * @param die the Die to check
     * @return the related error
     */
    public PlacementError isDieAllowed(int x, int y, Die die) {

        PlacementError ret = new PlacementError();

        // Check if the placed Die violates the restriction
        if(die == null){
            return ret;
        }

        // Check if the placed Die violate the restriction
        ret = PlacementError.union(ret, getCell(x, y).isDieAllowed(die));

        List<Die> adjacentDies = getNeighbours(x, y);

        // Check that all the adjacent dies don't have the same color or the same value of the placed die
        for (Die neighbour : adjacentDies) {
            if(neighbour != null && neighbour != die) {
                if (neighbour.getColor() == die.getColor())
                    ret = PlacementError.union(ret, new PlacementError(Flags.COLOR));
                if (neighbour.getValue() == die.getValue())
                    ret = PlacementError.union(ret, new PlacementError(Flags.VALUE));
            }
        }

        // Check if the Die is near other dices
        if (getNeighbours(x, y).isEmpty())
            ret = PlacementError.union(ret, new PlacementError(Flags.NEIGHBOURS));

        // Check if the Die has been placed on an edge
        if (x != 0 && y != 0 && x != 4 && y != 3)
            ret = PlacementError.union(ret, new PlacementError(Flags.EDGE));

        return ret;
    }

    /**
     * @param x first coordinate
     * @param y second coordinate
     * @return the Die in a specific position on the board
     */
    public Die getDie(int x, int y) {
        return getCell(x, y).getDie();
    }


    /**
     * @param x first coordinate of the Die object
     * @param y second coordinate of the Die object
     * @return the related Cell
     */
    public Cell getCell(int x, int y){
        if(!checkIndex(x, y))
            throw new IndexOutOfBoundsException();
        return window[y][x];
    }

    /**
     * @param x first coordinate of the Die object
     * @param y second coordinate of the Die object
     * @param die the Die object to set in the player's board
     */
    public void setDie(int x, int y, Die die) {
        getCell(x, y).setDie(die);
    }

    /**
     * This method returns the 4 Dice that are adjacent to a specific cell
     * @param x first coordinate of the Die
     * @param y second coordinate of the Die
     * @return Die[] containing the 4 adjacent dies
     */
    public List<Die> getNeighbours(int x, int y) {
        List<Die> adjacentDies = new ArrayList<>();

        int leftX = (x - 1 + boardWidth) % boardWidth;
        int rightX = (x + 1) % boardWidth;
        int aboveY = (y - 1 + boardHeight) % boardHeight;
        int belowY = (y + 1) % boardHeight;

        if(!getCell(leftX, y).isEmpty()) {
            adjacentDies.add(getDie(leftX, y));
        }

        if(!getCell(x, belowY).isEmpty()) {
            adjacentDies.add(getDie(x, belowY));
        }

        if(!getCell(rightX, y).isEmpty()) {
            adjacentDies.add(getDie(rightX, y));
        }

        if(!getCell(x, aboveY).isEmpty()) {
            adjacentDies.add(getDie(x, aboveY));
        }

        return adjacentDies;
    }

    /**
     * @param index index of the column we want the method to return
     * @return the index(th) column
     */
    public Cell[] getColumn(int index) {
        Cell[] col = new Cell[4];
        for(int row = 0; row < 4; row++)
            col[row] = getCell(index, row);
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
     * @return the board's difficulty
     */
    public int getBoardDifficulty() { return this.boardDifficulty; }

    /**
     * This method reset the board
     */
    private void reset() {
        for(Cell cell : this){
            cell.setDie(null);
        }
    }

    /**
     * @return how many dice there are on the board
     */
    public int countDice() {
        int counter = 0;
        for(Cell cell : this) {
            if (!cell.isEmpty()) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<Cell>() {
            private int index = -1;

            private int getX() {
                return index % 5;
            }

            private int getY() { return index / 5; /*integer division*/ }

            private int getNextX() {
                return (index + 1) % 5;
            }

            private int getNextY() {
                return (index + 1) / 5;
            }

            @Override
            public boolean hasNext() {
                return getNextX() < 5 && getNextY() < 4;
            }

            @Override
            public Cell next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                index++;
                return getCell(getX(), getY());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Helpers of Memento pattern
     */
    @Override
    public Board saveState() {
        return new Board(this);
    }

    @Override
    public void restoreState(Board savedState) {
        this.window = savedState.window;
        this.boardDifficulty = savedState.getBoardDifficulty();
    }

    /**
     * This method checks that the coordinates don't overflow the limit of the board
     * @param x first coordinate
     * @param y second coordinate
     * @return true or false
     */
    public static boolean checkIndex(int x, int y){
        return !(x < 0 || y < 0 || x > 4 || y > 3);
    }
}
