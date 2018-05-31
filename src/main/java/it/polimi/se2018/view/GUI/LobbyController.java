package it.polimi.se2018.view.GUI;

import it.polimi.se2018.network.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.StringConverter;

public class LobbyController {
    private Client client;

    @FXML
    private ProgressBar lobbyProgress;
    @FXML
    private Button playButton;
    @FXML
    private ChoiceBox<Integer> playerNumber;
    @FXML
    private Label waitingMessage;

    public void setClient(Client client) {
        this.client = client;
    }

    public void onPlayClick(ActionEvent actionEvent) {
        int numberOfPlayers = playerNumber.getSelectionModel().getSelectedItem();
        client.sendQueueRequest(numberOfPlayers);

        waitingMessage.setVisible(true);
        lobbyProgress.setVisible(true);
        playButton.setDisable(true);
    }

    @FXML
    public void initialize(){
        StringConverter<Integer> converter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object + " giocatori";
            }

            @Override
            public Integer fromString(String string) {
                return Character.getNumericValue(string.charAt(0));
            }
        };
        playerNumber.setConverter(converter);
        playerNumber.getItems().addAll(2, 3, 4);
        playerNumber.getSelectionModel().selectFirst();

        waitingMessage.setVisible(false);
        lobbyProgress.setVisible(false);
    }
}
