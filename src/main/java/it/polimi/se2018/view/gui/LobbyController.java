package it.polimi.se2018.view.gui;

import it.polimi.se2018.network.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 * JavaFX controller of the lobby phase
 */
public class LobbyController {
    private Client client;

    @FXML
    private ProgressBar lobbyProgress;
    @FXML
    private Button playButton;

    @FXML
    private Label waitingMessage;

    /**
     * Setter of Client
     * @param client Reference to the client object
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Send queue request to server. Display waiting messages
     */
    public void onPlayClick() {
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
