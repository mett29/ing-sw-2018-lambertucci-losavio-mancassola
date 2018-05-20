package it.polimi.se2018.model;

/**
 * This is the class that contains the board and the coordinates
 * @author mett29, MicheleLambertucci
 * @version 1.0
 */
public class BoardCoord implements DieCoord {

    private Board board;
    private int x;
    private int y;

    private Board savedBoard;

    public BoardCoord(Board board, int x, int y) {
        if(board == null){
            throw new NullPointerException();
        }
        this.board = board;
        if(!Board.checkIndex(x, y)){
            throw new IndexOutOfBoundsException("x must be in [0, 4]; y must be in [0, 3]");
        }
        this.x = x;
        this.y = y;
        this.savedBoard = null;
    }

    @Override
    public Die get() {
        return board.getDie(x, y);
    }

    @Override
    public void set(Die die) {
        board.setDie(x, y, die);
    }

    @Override
    public PlacementError isAllowed(Die die) {
        return board.isDieAllowed(x, y, die);
    }

    /**
     * Helpers of Memento pattern
     */
    @Override
    public void saveState() {
        savedBoard = board.saveState();
    }

    @Override
    public void restoreState() {
        if(savedBoard == null){ throw new NullPointerException(); }
        board.restoreState(savedBoard);
        savedBoard = null;
    }
}
