package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.message.PatternRequest;
import it.polimi.se2018.network.message.UndoResponse;
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

    private MatchController matchController;

    @Override
    public void onToolCardActivationResponse(boolean isOk) {

    }

    @Override
    public void updateMatch(Match match) {
        matchController.update(match);
    }

    @Override
    public void onConnect(boolean isOk) {
        // TODO: handle isOk true or false (username already exists)
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
    public void onReconnect(Match match) {
        //TODO
    }

    @Override
    public void onConnectionError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore di connessione");
        alert.setHeaderText("Si è verificato un errore di connessione");
        alert.setContentText("Verifica che la tua connessione sia attiva. Se lo è, forse il nome utente che hai scelto non è disponibile: scegline un altro.");

        e.printStackTrace();

        alert.showAndWait();
    }

    @Override
    public void onMatchStart(Match match) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/MatchGUI.fxml"));
            loader.setControllerFactory(c -> new MatchController(match, client));

            Parent matchParent = loader.load();
            matchController = loader.getController();
            stage.getScene().setRoot(matchParent);

            updateMatch(match);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPatternRequest(PatternRequest message) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/PatternPicker.fxml"));
            loader.setControllerFactory(c -> new PatternPickController(message.boards, client));
            Parent matchParent = loader.load();

            System.out.println(message.boards);



            stage.getScene().setRoot(matchParent);
        } catch(IOException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUndoResponse(UndoResponse message) {
        //TODO
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/LoginGUI.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);


        stage.setResizable(false);
        stage.show();



        this.stage = stage;

        this.client = new Client();
        client.setView(this);

        LoginController loginController = loader.getController();
        loginController.setClient(client);
    }
}
