package it.polimi.se2018.model;

//The class that describes the objective card (private and public)
public abstract class ObjCard extends Card {
    public ObjCard(){}

    /**
     * Get the description of the card
     * @return description
     */
    public abstract String getDescription();

    /**
     * Get the name/title of the card
     * @return title
     */
    public abstract String getTitle();

    /**
     * Calculate the bonus granted by the Objective Card to the board
     * @param board Board to be judged
     * @return bonus score of the board
     */
    public abstract int getBonus(Board board);
}
