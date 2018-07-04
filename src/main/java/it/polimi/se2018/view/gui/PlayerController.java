package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Set;

public class PlayerController {
    private final boolean isMe;
    private final Client client;



    private Player player;
    private BoardGUIController boardController;
    private CellController pickedController;

    public PlayerController(Player player, boolean isMe, Client client){
        this.player = player;
        this.isMe = isMe;
        this.client = client;
    }

    public void update(Player player){
        this.player = player;
        Platform.runLater(() -> playerName.setText(player.getName() + (player.isDisconnected() ? " (DISCONN.)" : "")));
        tokens.setProgress(player.getToken() / 10D);

        Board board = player.getBoard();
        boardController.update(board);
        updatePicked(player.getPickedDie());
    }

    @FXML
    private VBox playerInfoContainer;
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

        playerName.setText(player.getName() + (player.isDisconnected() ? " (DISCONN.)" : ""));
        tokens.setProgress(player.getToken() / 10D);

        // deactivate pane if it isn't "mine"
        borderPane.setDisable(!isMe);


        loader = new FXMLLoader(getClass().getResource("/CellGUI.fxml"));
        loader.setControllerFactory(e -> new CellController(new Cell(null), false));
        try {
            playerInfoContainer.getChildren().add(loader.load());
            pickedController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display private object card color
        if(isMe){
            Color privateColor = player.getPrivateObjCard().getColor();
            loader = new FXMLLoader(getClass().getResource("/CellGUI.fxml"));
            loader.setControllerFactory(c -> new CellController(new Cell(new Restriction(privateColor)), false));
            try {
                playerInfoContainer.getChildren().add(loader.load());
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    private void updatePicked(Die die) {
        Cell pp = new Cell(null);
        pp.setDie(die);

        pickedController.update(pp);
    }

    void activate(Set<CellState> cellStates) {
        boardController.activate(cellStates);
    }

    void disableAll(){
        boardController.disableAll();
    }
}
