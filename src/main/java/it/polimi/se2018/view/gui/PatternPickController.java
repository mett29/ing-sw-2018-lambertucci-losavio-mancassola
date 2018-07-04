package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class PatternPickController {
    private final Client client;
    private PrivateObjCard privateObjCard;
    @FXML
    private GridPane boardGrid;
    @FXML
    private VBox playerInfos;

    private List<Board> boards;

    public PatternPickController(List<Board> boards, Client client, PrivateObjCard privateObjCard) {
        this.boards = boards;
        this.client = client;
        this.privateObjCard = privateObjCard;
    }

    @FXML
    public void initialize(){
        // Add player infos
        playerInfos.getChildren().add(new Label(client.getUsername()));

        Color privateColor = privateObjCard.getColor();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CellGUI.fxml"));
        loader.setControllerFactory(c -> new CellController(new Cell(new Restriction(privateColor)), false));
        try {
            playerInfos.getChildren().add(loader.load());
        } catch(IOException e){
            e.printStackTrace();
        }


        // Add boards
        int iterator = 0;
        for (Board board : boards) {
            try {
                loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/BoardGUI.fxml"));
                loader.setControllerFactory(c -> new BoardGUIController(board, client));
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

        boardGrid.setGridLinesVisible(true);
    }
}
