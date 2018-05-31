package it.polimi.se2018.view.GUI;

import it.polimi.se2018.network.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class LoginController {
    @FXML
    public TextField username;

    @FXML
    public ChoiceBox connectionTypeBox;
    public AnchorPane content;

    private Client client;

    @FXML
    public void initialize(){
        connectionTypeBox.getItems().removeAll(connectionTypeBox.getItems());
        connectionTypeBox.getItems().addAll("Connessione con RMI", "Connessione con Socket");
        connectionTypeBox.getSelectionModel().select(0);
    }

    public void setClient(Client client){
        this.client = client;
    }

    @FXML
    public void onClickLogin(ActionEvent actionEvent) {
        client.setUsername(username.getCharacters().toString());
        int selectedIndex = connectionTypeBox.getSelectionModel().getSelectedIndex();
        client.setConnection(selectedIndex == 0);

        try {
            client.connect();
        } catch (Exception e) {
            client.onConnectionError();
        }
    }
}
