package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.network.message.PatternRequest;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;

public class ChoosePatternController {

    @FXML
    private GridPane boardBox;

    private List<Board> boards;

    ChoosePatternController(PatternRequest request) {
        boards = request.boards;
    }

    @FXML
    public void initialize(){
        int iterator = 0;
        for (Board board : boards) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/BoardGUI.fxml"));
                loader.setController(new BoardGUIController(board));
                Parent node = loader.load();
                boardBox.add(node, iterator % 2, iterator / 2);
            } catch(IOException e){
                e.printStackTrace();
            }
            iterator++;
        }
    }
}
