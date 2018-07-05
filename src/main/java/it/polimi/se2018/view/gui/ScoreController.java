package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.Score;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * JavaFX controller of the Score section
 */
public class ScoreController {
    @FXML
    private GridPane scoreGrid;

    private List<Player> players;
    private Map<Player, Score> scores;

    /**
     * Constructor
     * @param match Match from which read scores
     */
    ScoreController(Match match) {
        players = match.getPlayers();
        scores = players.stream().collect(Collectors.toMap(Function.identity(), match::getScore));
    }

    public void initialize(){
        for(int i = 0; i < players.size(); i++) {
            VBox scoreBoard = new VBox();
            scoreBoard.getChildren().add(new Label("Username: " + players.get(i).getName()));
            scoreBoard.getChildren().add(new Label("Private Objective Card: " + scores.get(players.get(i)).getValues()[0]));
            scoreBoard.getChildren().add(new Label("Public Objective Card: " + scores.get(players.get(i)).getValues()[1]));
            scoreBoard.getChildren().add(new Label("Tokens: " + scores.get(players.get(i)).getValues()[2]));
            scoreBoard.getChildren().add(new Label("Empty cells: " + scores.get(players.get(i)).getValues()[3]));
            scoreBoard.getChildren().add(new Label("Total: " + scores.get(players.get(i)).getOverallScore()));
            scoreBoard.getChildren().add(new Label(""));
            scoreBoard.getChildren().add(new Label(players.get(i).isWinner() ? "       (WINNER!)" : ""));

            scoreGrid.add(scoreBoard, i / 2, i % 2);
        }
    }
}
