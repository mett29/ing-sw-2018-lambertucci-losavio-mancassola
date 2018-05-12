package it.polimi.se2018.model;

import java.security.InvalidParameterException;
import java.util.Random;

public class Die {
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
     * COpy constructor
     * @param die object to deep copy
     */
    public Die(Die die){
        if(die == null)
            throw new NullPointerException("'die' must be not null");
        this.value = die.getValue();
        this.color = die.getColor();
    }

    /**
     * Getter of the Die value
     * @return the value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Getter of the Die color
     * @return the color
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
}
