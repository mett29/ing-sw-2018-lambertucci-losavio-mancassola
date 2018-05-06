package it.polimi.se2018.model;

import java.util.Random;

public class Die {
    private Color color;
    private int value;

    public Die(int value, Color color) {
        this.value = value;
        this.color = color;
    }

    /**
     * COpy constructor
     * @param die object to deep copy
     */
    public Die(Die die){
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
