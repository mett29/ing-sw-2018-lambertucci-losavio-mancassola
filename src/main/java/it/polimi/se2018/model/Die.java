package it.polimi.se2018.model;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.Random;

/**
 * This class describes the object Die
 * @author ontech7
 */
public class Die implements Serializable{
    private Color color;
    private int value;

    public Die(int value, Color color) {
        if(value < 0 || value > 6)
            throw new InvalidParameterException("The die value must be between 1 and 6 (0 for null)");
        this.value = value;
        if(color == null)
            throw new NullPointerException("'color' must be not null");
        this.color = color;
    }

    /**
     * Copy constructor
     * @param die object to deep copy
     */
    public Die(Die die){
        if(die == null)
            throw new NullPointerException("'die' must be not null");
        this.value = die.getValue();
        this.color = die.getColor();
    }

    /**
     * @return the Die's value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * @return the Die's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * Randomize the value of the Die between 1 and 6
     */
    public void randomize() {
        this.value = new Random().nextInt(6) + 1;
    }

    @Override
    public String toString() {
        return "(" + this.value + "," + this.color + ")";
    }
}
