package it.polimi.se2018.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import it.polimi.se2018.model.Board;
import com.google.gson.Gson;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to load the boards' json configuration files and parse them into Board objects
 * @author mett29
 */
public class JsonParser {

    // Contains all the ParsedBoard
    private List<ParsedBoard> parsedBoards;

    // Constructor
    public JsonParser() throws IOException {
        this.parsedBoards = loadParsedBoards();
    }

    /**
     * This method loads and parses the json file containing all the boards using GSON library
     * @return the List of parsed Boards
     * @throws IOException
     */
    private List<ParsedBoard> loadParsedBoards() throws IOException {
        Gson gson = new GsonBuilder().create();
        JsonReader jsonBoard = new JsonReader(new FileReader("src\\main\\res\\boards.json"));
        List<ParsedBoard> parsedBoards = gson.fromJson(jsonBoard, ParsedBoard[].class);
        jsonBoard.close();
        return parsedBoards;
    }

    /**
     * @return an array containing all the Parsed Boards
     */
    public List<ParsedBoard> getParsedBoards() {
        return parsedBoards;
    }
}

// These classes are needed by GSON for the loading process
class ParsedBoard {
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

class ColorRestrictions {
    private String color;
    private int[][] coords;

    public String getColor() {
        return color;
    }

    public int[][] getCoords() {
        return coords;
    }
}

class ValueRestrictions {
    private Integer value;
    private int[][] coords;

    public Integer getValue() {
        return value;
    }

    public int[][] getCoords() {
        return coords;
    }
}
