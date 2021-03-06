package it.polimi.se2018.network.server;

import it.polimi.se2018.model.Board;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * These classes are needed by GSON for the loading process
 * @author mett29
 */
public class ParsedBoard implements Serializable{
    private List<Board> boards = new ArrayList<>();
    private String name;
    private int difficulty;
    private List<ColorRestrictions> colorRestrictions;
    private List<ValueRestrictions> valueRestrictions;


    public List<Board> getBoards() {
        return boards;
    }

    public String getName() {
        return name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public List<ColorRestrictions> getColorRestrictions() {
        return colorRestrictions;
    }

    public List<ValueRestrictions> getValueRestrictions() {
        return valueRestrictions;
    }
}

class ValueRestrictions implements Serializable{
    private Integer value;
    private int[][] coords;

    public Integer getValue() {
        return value;
    }

    public int[][] getCoords() {
        return coords;
    }
}

class ColorRestrictions implements Serializable{
    private String color;
    private int[][] coords;

    public String getColor() {
        return color;
    }

    public int[][] getCoords() {
        return coords;
    }
}
