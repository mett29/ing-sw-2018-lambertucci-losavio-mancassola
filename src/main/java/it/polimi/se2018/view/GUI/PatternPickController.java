package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.network.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.List;

public class PatternPickController {
    private final Client client;
    public GridPane boardGrid;

    private List<Board> boards;

    public PatternPickController(List<Board> boards, Client client) {
        this.boards = boards;
        this.client = client;
    }

    @FXML
    public void initialize(){
        int iterator = 0;
        for (Board board : boards) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/BoardGUI.fxml"));
                loader.setControllerFactory(c -> new BoardGUIController(board));
                Parent node = loader.load();
                final int tmpIterator = iterator;
                node.setOnMouseClicked(event -> {
                    boardGrid.setDisable(true);
                    client.sendPatternResponse(tmpIterator);
                });
                boardGrid.add(node, iterator % 2, iterator / 2);
            } catch(IOException e){
                e.printStackTrace();
            }
            iterator++;
        }
    }
}
