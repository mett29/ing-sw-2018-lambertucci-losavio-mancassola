package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.view.ViewInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application implements ViewInterface {

    private Client client;

    private LoginController loginController;

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

    }

    @Override
    public void onConnectionError() {

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

        this.client = new Client();
        client.setView(this);

        loginController = loader.getController();
        loginController.setClient(client);
    }
}
