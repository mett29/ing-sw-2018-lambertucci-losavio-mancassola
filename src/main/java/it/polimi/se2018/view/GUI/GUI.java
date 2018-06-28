package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.message.PatternRequest;
import it.polimi.se2018.network.message.UndoResponse;
import it.polimi.se2018.view.ViewInterface;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
        if(isOk) {
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
        } else {
            Platform.runLater(
                () -> {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Username già preso");
                    alert.setHeaderText("Scegli un username differente");

                    alert.showAndWait();
                }
            );
        }
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
    public void onMatchStart(Match match, int timerValue) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/MatchGUI.fxml"));


            loader.setControllerFactory(c -> new MatchController(match, client, timerValue));
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
            loader.setControllerFactory(c -> new PatternPickController(message.boards, client, message.privateObjCard));
            Parent matchParent = loader.load();

            stage.getScene().setRoot(matchParent);
        } catch(IOException e){
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUndoResponse(UndoResponse message) {
        if(!message.ok){
            Alert unableToUnload = new Alert(Alert.AlertType.ERROR);
            unableToUnload.setTitle("Impossibile annullare");
            unableToUnload.setHeaderText("È impossibile annullare l'azione in questo momento");

            unableToUnload.show();
        }
    }

    @Override
    public void onTimeReset() {
        matchController.onTimeReset();
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

        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });

        this.stage = stage;

        this.client = new Client();
        client.setView(this);

        LoginController loginController = loader.getController();
        loginController.setClient(client);
    }
}
