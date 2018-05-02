package it.polimi.se2018.model;

/**
 * This class represents the Restriction of color and value present in the board
 * @version 1.0
 */
public class Restriction {
    private Color color;
    private int value;

    public Restriction(Color color, int value) {
        this.color = color;
        this.value = value;
    }

    //gets any error from color and/or value restrictions
    public PlacementError isDieAllowed(Die die) {
        if (die.getColor() != color)
            return new PlacementError(Flags.COLOR);
        if (die.getValue() != value)
            return new PlacementError(Flags.VALUE);
        return new PlacementError();
    }


}
