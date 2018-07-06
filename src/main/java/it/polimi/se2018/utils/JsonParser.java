package it.polimi.se2018.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;
import it.polimi.se2018.controller.Configuration;
import it.polimi.se2018.network.server.ParsedBoard;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class is used to load the boards' json configuration files and parse them into Board objects
 * @author mett29
 */
public class JsonParser {

    // Contains all the ParsedBoards
    private List<ParsedBoard> parsedBoards;

    // Constructor
    public JsonParser() throws IOException {
        this.parsedBoards = loadParsedBoards();
    }

    /**
     * This method loads and parses the json file containing all the boards using GSON library
     * @return the List of parsed Boards
     * @throws IOException exception of input/output
     */
    private List<ParsedBoard> loadParsedBoards() throws IOException {
        // Loading the standard patterns' file
        Gson gson = new GsonBuilder().create();
        JsonReader jsonBoard = new JsonReader(new InputStreamReader(getClass().getResourceAsStream("/boards.json")));
        ParsedBoard[] parsedBoardsArray = gson.fromJson(jsonBoard, ParsedBoard[].class);
        jsonBoard.close();

        // Loading the patterns' file specified by user
        String patternPath = Configuration.getInstance().getPatternPath();
        if (!patternPath.equals("null")) {
            String dir = System.getProperty("user.dir");
            JsonReader jsonUserBoard = new JsonReader(new FileReader(dir + "/" + patternPath));
            ParsedBoard[] userParsedBoards = gson.fromJson(jsonUserBoard, ParsedBoard[].class);
            // Join files
            parsedBoardsArray = Stream.concat(Arrays.stream(parsedBoardsArray), Arrays.stream(userParsedBoards))
                    .toArray(ParsedBoard[]::new);
            jsonUserBoard.close();
        }

        return Arrays.asList(parsedBoardsArray);
    }

    /**
     * @return an array containing all the Parsed Boards
     */
    public List<ParsedBoard> getParsedBoards() {
        return parsedBoards;
    }
}