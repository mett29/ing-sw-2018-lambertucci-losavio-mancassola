package it.polimi.se2018.view.gui;

import it.polimi.se2018.network.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class LobbyController {
    private Client client;

    @FXML
    private ProgressBar lobbyProgress;
    @FXML
    private Button playButton;

    @FXML
    private Label waitingMessage;

    public void setClient(Client client) {
        this.client = client;
    }

    public void onPlayClick(ActionEvent actionEvent) {
        client.sendQueueRequest();

        waitingMessage.setVisible(true);
        lobbyProgress.setVisible(true);
        playButton.setDisable(true);
    }

    @FXML
    public void initialize(){
        waitingMessage.setVisible(false);
        lobbyProgress.setVisible(false);
    }
}
