package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.view.ViewInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application implements ViewInterface {

    private Client client;

    private Stage stage;

    @Override
    public void askLogin() {

    }

    @Override
    public void askTypeOfConnection() {

    }

    @Override
    public void onToolCardActivationResponse(boolean isOk) {

    }

    @Override
    public void updateMatch(Match match) {

    }

    @Override
    public void onConnect() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/LobbyGUI.fxml"));
            Parent lobby = loader.load();

            LobbyController controller = loader.getController();
            controller.setClient(client);

            Stage mainStage = stage;
            mainStage.getScene().setRoot(lobby);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di login");
        alert.setHeaderText("Si è verificato un errore di login");
        alert.setContentText("Verifica che la tua connessione sia attiva. Se lo è, il nome utente che hai scelto non è disponibile: scegline un altro.");

        alert.showAndWait();
    }

    @Override
    public void waitFor() {

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/LoginGUI.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        this.stage = stage;

        this.client = new Client();
        client.setView(this);

        LoginController loginController = loader.getController();
        loginController.setClient(client);
    }
}