package it.polimi.se2018.model;

import java.io.Serializable;

/**
 * This class represents the Restriction of color and value present in the board
 * @version 1.1
 * @author mett29
 */
public class Restriction implements Serializable{
    private Color color;
    private int value;
    private RestrictionType type;

    public Restriction(Color color) {
        this.color = color;
        type = RestrictionType.COLOR;
    }

    public Restriction(int value){
        this.value = value;
        type = RestrictionType.VALUE;
    }

    /**
     * Check possible errors if `die` is to be evaluated by this restriction
     * @param die to be evaluated
     * @return errors container
     */
    public PlacementError isDieAllowed(Die die) {
        switch(type){
            case COLOR:
                if(die.getColor() != color)
                    return new PlacementError(Flag.COLOR);
                else return new PlacementError();

            case VALUE:
                if(die.getValue() != value)
                    return new PlacementError(Flag.VALUE);
                else return new PlacementError();

            default:
                return new PlacementError();
        }
    }

    private enum RestrictionType implements Serializable {
        COLOR, VALUE
    }

    @Override
    public String toString() {
        if(type == RestrictionType.COLOR){
            return this.color.toString();
        } else {
            return "" + this.value;
        }
    }
}
