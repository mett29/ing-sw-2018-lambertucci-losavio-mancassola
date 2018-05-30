package it.polimi.se2018.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;
import it.polimi.se2018.network.server.ParsedBoard;

import java.io.FileReader;
import java.io.IOException;
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