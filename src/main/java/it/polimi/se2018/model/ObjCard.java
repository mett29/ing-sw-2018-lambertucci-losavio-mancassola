package it.polimi.se2018.model;

//The class that describes the objective card (private and public)
public interface ObjCard extends Card {
    /**
     * Calculate the bonus granted by the Objective Card to the board
     * @param board Board to be judged
     * @return bonus score of the board
     */
    public int getBonus(Board board);
}
