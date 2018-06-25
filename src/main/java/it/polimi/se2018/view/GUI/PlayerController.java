package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.CellState;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.network.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.EnumSet;

public class PlayerController {
    private final boolean isMe;
    private final Client client;
    private Player player;
    private BoardGUIController boardController;

    public PlayerController(Player player, boolean isMe, Client client){
        this.player = player;
        this.isMe = isMe;
        this.client = client;
    }

    public void update(Player player){
        this.player = player;
        playerName.setText(player.getName());
        tokens.setProgress(player.getToken() / 10D);

        Board board = player.getBoard();
        boardController.update(board);
    }

    @FXML
    private Label playerName;
    @FXML
    private ProgressBar tokens;
    @FXML
    private BorderPane borderPane;

    @FXML
    public void initialize(){
        Board board = player.getBoard();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/BoardGUI.fxml"));
        loader.setControllerFactory(c -> new BoardGUIController(board, client));

        try {
            borderPane.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boardController = loader.getController();

        playerName.setText(player.getName());
        tokens.setProgress(player.getToken() / 10D);

        // deactivate pane if it isn't "mine"
        borderPane.setDisable(!isMe);
    }

    void activate(EnumSet<CellState> cellStates) {
        boardController.activate(cellStates);
    }

    void disableAll(){
        boardController.disableAll();
    }
}
