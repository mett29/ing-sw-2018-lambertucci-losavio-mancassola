package it.polimi.se2018.view;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.CellState;
import it.polimi.se2018.model.DiceContainer;

import java.util.Set;

public class Utils {
    private Utils(){}
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
}
