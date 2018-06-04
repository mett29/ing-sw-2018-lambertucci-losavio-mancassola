package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class BoardGUIController {
    @FXML
    private GridPane gridPane;

    private Board board;
    BoardGUIController(Board board) {
        this.board = board;
    }

    @FXML
    public void initialize(){
        for(int x = 0; x < 5; x ++){
            for(int y = 0; y < 4; y++){
                AnchorPane raga = new AnchorPane();
                raga.getChildren().add(new Label(board.getCell(x, y).getRestriction().toString()));
                gridPane.add(raga, x, y);
            }
        }
    }
}
