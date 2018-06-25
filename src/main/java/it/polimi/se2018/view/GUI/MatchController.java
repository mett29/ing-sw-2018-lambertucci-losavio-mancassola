package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class MatchController {

    private Match match;

    @FXML
    private GridPane boardGrid;
    @FXML
    private AnchorPane draftpoolContainer;
    @FXML
    private AnchorPane roundTrackerContainer;
    @FXML
    private VBox toolcardContainer;
    @FXML
    private Button passBtn;
    @FXML
    private Button normalMoveBtn;
    @FXML
    private Button undoBtn;

    private Client client;

    private Map<String, PlayerController> playerControllers;
    private Map<String, AnchorPane> panes;
    private DiceContainerController roundTrackerController;
    private DiceContainerController draftPoolController;


    public MatchController(Match match, Client client){
        panes = new HashMap<>();
        this.client = client;

        playerControllers = new HashMap<>();
        for(Player p : match.getPlayers()){
            playerControllers.put(p.getName(), new PlayerController(p, p.getName().equals(client.getUsername()), client));
        }
        for (Map.Entry<String, PlayerController> player : playerControllers.entrySet()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/PlayerGUI.fxml"));
            loader.setControllerFactory(c -> player.getValue());
            try {
                AnchorPane playerGui = loader.load();
                panes.put(player.getKey(), playerGui);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.match = match;
    }

    private void switchStates(Match match){
        PlayerState state = match.getPlayerByName(client.getUsername()).getState();
        switch(state.get()){
            case IDLE:
                break;
            case YOUR_TURN:
                onYourTurn(match);
                break;
            case PICK:
                onPickState(match);
                break;
            case VALUE:
                onValueState();
                break;
            case UPDOWN:
                onUpDownState();
                break;
            case YESNO:
                onYesNoState();
                break;
            case REPEAT:
                onRepeatState(this.match);
                break;
        }
    }

    public void update(Match match){
        for(Player player : match.getPlayers()) {
            playerControllers.get(player.getName()).update(player);
        }

        draftPoolController.update(match.getDraftPool());
        roundTrackerController.update(match.getRoundTracker());

        normalMoveBtn.setDisable(true);
        passBtn.setDisable(true);

        switchStates(match);


        if(match.getPlayerByName(client.getUsername()).getState().get() != EnumState.REPEAT){
            this.match = match;
        }
    }

    private void onRepeatState(Match oldMatch) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Mossa non valida");
            alert.setHeaderText("La mossa che hai fatto non è valida");
            alert.setContentText("Mi raccomando stai attento eh");

            alert.show();
        });

        switchStates(oldMatch);
    }

    private void onYesNoState() {
        Platform.runLater(() -> {
            List<String> choices = new ArrayList<>();
            choices.add("Si");
            choices.add("No");

            ChoiceDialog<String> dialog = new ChoiceDialog<>("+1", choices);
            dialog.setTitle("Toolcard");
            dialog.setHeaderText("Vuoi fare la prossima mossa?");
            dialog.setContentText("Scelta:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(value -> {
                client.sendMove(new YesNoMove(choices.indexOf(value) == 0));
            });
        });

    }

    private void onUpDownState() {
        Platform.runLater(() -> {
            List<String> choices = new ArrayList<>();
            choices.add("+1");
            choices.add("-1");

            ChoiceDialog<String> dialog = new ChoiceDialog<>("+1", choices);
            dialog.setTitle("Toolcard");
            dialog.setHeaderText("Vuoi aggiungere o sottrarre uno al dado?");
            dialog.setContentText("Scelta:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(value -> {
                client.sendMove(new UpDownMove(choices.indexOf(value) == 0));
            });
        });

    }

    private void onValueState() {
        Platform.runLater(() -> {
            List<String> choices = new ArrayList<>();
            choices.add("1");
            choices.add("2");
            choices.add("3");
            choices.add("4");
            choices.add("5");
            choices.add("6");

            ChoiceDialog<String> dialog = new ChoiceDialog<>("1", choices);
            dialog.setTitle("Toolcard");
            dialog.setHeaderText("Scegli il valore che vuoi assegnare");
            dialog.setContentText("Valore:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(value -> {
                client.sendMove(new ValueMove(choices.indexOf(value) + 1));
            });
        });

    }

    private void onPickState(Match newMatch){
        toolcardContainer.setDisable(true);
        PickState state = (PickState) newMatch.getPlayerByName(client.getUsername()).getState();
        if(state.getActiveContainers().contains(Component.DRAFTPOOL)){
            draftPoolController.activate(state.getCellStates());
        }
        if(state.getActiveContainers().contains(Component.BOARD)){
            playerControllers.get(client.getUsername()).activate(state.getCellStates());
        }

        undoBtn.setDisable(false);
    }

    private void onYourTurn(Match newMatch) {
        EnumSet<PossibleAction> actions = newMatch.getPlayerByName(client.getUsername()).getPossibleActions();
        if(actions.contains(PossibleAction.ACTIVATE_TOOLCARD))
            toolcardContainer.setDisable(false);
        if(actions.contains(PossibleAction.PICK_DIE))
            normalMoveBtn.setDisable(false);
        if(actions.contains(PossibleAction.PASS_TURN))
            passBtn.setDisable(false);

        normalMoveBtn.setOnMouseClicked(e -> {
            client.activateNormalMove();
            normalMoveBtn.setDisable(true);
        });

        passBtn.setOnMouseClicked(e -> {
            client.pass();
            passBtn.setDisable(true);
        });
    }

    @FXML
    public void initialize(){
        int index = 0;
        boardGrid.setGridLinesVisible(true);
        for (AnchorPane pane : panes.values()) {
            boardGrid.add(pane, index / 2, index % 2);
            index++;
        }

        // load draftpool container
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/DiceContainerGUI.fxml"));
        loader.setControllerFactory(c -> new DiceContainerController(match.getDraftPool(), DiceContainerCoordMove.DiceContainerName.DRAFT_POOL, client));
        try {
            draftpoolContainer.getChildren().add(loader.load());
            draftPoolController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load roundtracker container
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/DiceContainerGUI.fxml"));
        loader.setControllerFactory(c -> new DiceContainerController(match.getRoundTracker(), DiceContainerCoordMove.DiceContainerName.ROUND_TRACKER, client));
        try {
            roundTrackerContainer.getChildren().add(loader.load());
            roundTrackerController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 3; i++){
            final int toolcardIndex = i;
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ToolCardGUI.fxml"));
            loader.setControllerFactory(c -> new ToolCardController(match.getToolCards()[toolcardIndex], toolcardIndex, client));

            try {
                toolcardContainer.getChildren().add(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }

            toolcardContainer.setDisable(true);
        }

        passBtn.setDisable(true);
        undoBtn.setDisable(true);
        undoBtn.setOnMouseClicked(e -> {
            client.sendUndoRequest();
            draftPoolController.disableAll();
            roundTrackerController.disableAll();
            playerControllers.get(client.getUsername()).disableAll();
            undoBtn.setDisable(true);
        });
    }
}
