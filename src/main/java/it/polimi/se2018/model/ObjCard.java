package it.polimi.se2018.model;

/**
 * Interface that describes the object ObjCard (both private and public)
 * Implemented by {@link PrivateObjCard} and {@link PublicObjCard}
 * Extends the interface {@link Card}
 * @author MicheleLambertucci
 */
public interface ObjCard extends Card {
    /**
     * Calculate the bonus granted by the Objective Card to the board
     * @param board Board to be judged
     * @return bonus score of the board
     */
    public int getBonus(Board board);
}
