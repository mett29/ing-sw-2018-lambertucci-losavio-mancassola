package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.*;
import it.polimi.se2018.network.server.CountdownTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX controller of the match phase
 */
public class MatchController {

    private static final String TOOLCARD_MESSAGE_TITLE = "Toolcard";

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
    @FXML
    private VBox publicObjContainer;

    private Client client;

    private Map<String, PlayerController> playerControllers;
    private Map<String, AnchorPane> panes;
    private DiceContainerController roundTrackerController;
    private DiceContainerController draftPoolController;

    private CountdownTimer timer;

    private ProgressBar timerBar;

    private static Logger logger = Logger.getLogger("matchController");


    /**
     * Constructor
     * @param match      Match to be displayed
     * @param client     Reference to the Client object
     * @param timerValue Initial in-game timer value
     */
    MatchController(Match match, Client client, int timerValue){
        panes = new HashMap<>();

        timerBar = new ProgressBar(1d);

        this.client = client;
        this.timer = new CountdownTimer(timerValue,
                () -> { /*do nothing*/ },
                () -> Platform.runLater(() -> timerBar.setProgress(1d)),
                () -> Platform.runLater(() -> timerBar.setProgress(timerBar.getProgress() - 1d/timerValue))
        );

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
                logger.log(Level.WARNING,e.getMessage());
            }
        }

        this.match = match;

        timer.start();
    }

    /**
     * Call the corresponding handler of the new PlayerState
     * @param match New match state
     */
    private void switchStates(Match match){
        PlayerState state = match.getPlayerByName(client.getUsername()).getState();
        switch(state.get()){
            case IDLE:
                disableAll();
                break;
            case YOUR_TURN:
                activatePossibleActions(match);
                break;
            case PICK:
                handlePickState(match);
                break;
            case VALUE:
                askValue();
                break;
            case UPDOWN:
                askUpDown();
                break;
            case YESNO:
                askYesNo();
                break;
            case REPEAT:
                onRepeatState(this.match);
                break;
        }
    }

    /**
     * Disable each component
     */
    private void disableAll() {
        setDisableComponents(true, true, true, true);
    }

    /**
     * Update view to display the new state
     * @param match The new match state
     */
    void update(Match match){
        if(match.isFinished()){
            Platform.runLater(() -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ScoreGUI.fxml"));
                    loader.setControllerFactory(c -> new ScoreController(match));
                    Stage stage = new Stage();
                    Scene scene = new Scene(loader.load());
                    stage.setTitle("Risultati");
                    stage.setScene(scene);
                    stage.show();
                } catch (IOException e) {
                    logger.log(Level.WARNING,e.getMessage());
                }
            });
        } else {
            for (Player player : match.getPlayers()) {
                playerControllers.get(player.getName()).update(player);
            }

            draftPoolController.update(match.getDraftPool());
            roundTrackerController.update(match.getRoundTracker());

            setDisableComponents(true, true, true, true);

            switchStates(match);


            if (match.getPlayerByName(client.getUsername()).getState().get() != EnumState.REPEAT) {
                this.match = match;
            }
        }
    }

    /**
     * After displaying an error message, calls handler of the previous state
     * @param oldMatch Previous match state
     */
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

    /**
     * Display a Yes/No dialog and sends the response to the Server
     */
    private void askYesNo() {
        ask(
                "Vuoi fare la prossima mossa?",
                "Scelta:",
                Arrays.asList("Si", "No"),
                value -> client.sendMove(new YesNoMove(value == 0))
        );
    }

    /**
     * Display a +1/-1 dialog and sends the response to the Server
     */
    private void askUpDown() {
        ask(
                "Vuoi aggiungere o sottrarre uno al dado?",
                "Scelta:",
                Arrays.asList("+1", "-1"),
                value -> client.sendMove(new UpDownMove(value == 0))
        );
    }

    /**
     * Display a dialog in which the user needs to chose from a list of possible choices. The dialog cannot be closed unless "ok" button is clicked.
     * @param header      Header text message of the dialog
     * @param content     Content text message of the dialog
     * @param choices     List of choices to pick from
     * @param onSelected  When "ok" is clicked, this consumer will be executed. The argument will be the index of the selected item in the list
     */
    private void ask(String header, String content, List<String> choices, Consumer<Integer> onSelected){
        Platform.runLater(() -> {
            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setTitle(TOOLCARD_MESSAGE_TITLE);
            dialog.setHeaderText(header);
            dialog.setContentText(content);

            dialog.getDialogPane().getScene().getWindow().setOnCloseRequest(Event::consume);
            dialog.getDialogPane().lookupButton(ButtonType.CANCEL).addEventFilter(ActionEvent.ACTION, Event::consume);

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(value -> onSelected.accept(choices.indexOf(value)));
        });
    }

    /**
     * Display a "pick value" dialog and sends the response to the Server
     */
    private void askValue() {
        ask(
                "Scegli il valore che vuoi assegnare",
                "Valore:",
                Arrays.asList("1", "2", "3", "4", "5", "6"),
                value -> client.sendMove(new ValueMove(value + 1))
        );
    }

    /**
     * Activate each container cell that is said to be active in `newMatch`
     * @param newMatch current match state
     */
    private void handlePickState(Match newMatch){
        PickState state = (PickState) newMatch.getPlayerByName(client.getUsername()).getState();
        if(state.getActiveContainers().contains(Component.DRAFTPOOL)){
            draftPoolController.activate(state.getCellStates());
        }
        if(state.getActiveContainers().contains(Component.BOARD)){
            playerControllers.get(client.getUsername()).activate(state.getCellStates());
        }
        if (state.getActiveContainers().contains(Component.ROUNDTRACKER)) {
            roundTrackerController.activate(state.getCellStates());
        }

        setDisableComponents(null, null, true, false);
    }

    /**
     * Activate each button that is said to be active in `newMatch`
     * @param newMatch New match state
     */
    private void activatePossibleActions(Match newMatch) {
        Set<PossibleAction> actions = newMatch.getPlayerByName(client.getUsername()).getPossibleActions();
        setDisableComponents(!actions.contains(PossibleAction.PICK_DIE), !actions.contains(PossibleAction.PASS_TURN), !actions.contains(PossibleAction.ACTIVATE_TOOLCARD), true);

        normalMoveBtn.setOnMouseClicked(e -> client.activateNormalMove());

        passBtn.setOnMouseClicked(e -> client.pass());
    }

    /**
     * Call `setDisable` in each component. Each parameter can be set to `true` (which disable the button), `false` (which activates the button) or `null` (which leaves the button as is).
     * @param normalMove        Normal move button parameter
     * @param passButton        Pass button parameter
     * @param toolCardContainer Toolcard buttons parameter (all "Usa" buttons)
     * @param undoButton        Undo button parameter
     */
    private synchronized void setDisableComponents(Boolean normalMove, Boolean passButton, Boolean toolCardContainer, Boolean undoButton){
        if(normalMove != null)
            normalMoveBtn.setDisable(normalMove);
        if(passButton != null)
            passBtn.setDisable(passButton);
        if(toolCardContainer != null)
            toolcardContainer.setDisable(toolCardContainer);
        if(undoButton != null)
            undoBtn.setDisable(undoButton);
    }

    /**
     * Disable the `pass` button, the `normal move` button and each toolcard `use` button
     */
    private void disableNormTcPassUndo(){
        setDisableComponents(true, true, true, true);
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
            logger.log(Level.WARNING,e.getMessage());
        }

        // load roundtracker container
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/DiceContainerGUI.fxml"));
        loader.setControllerFactory(c -> new DiceContainerController(match.getRoundTracker(), DiceContainerCoordMove.DiceContainerName.ROUND_TRACKER, client));
        try {
            roundTrackerContainer.getChildren().add(loader.load());
            roundTrackerController = loader.getController();
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        }

        // load toolcards
        for(int i = 0; i < 3; i++){
            final int toolcardIndex = i;
            loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/ToolCardGUI.fxml"));
            loader.setControllerFactory(c -> new ToolCardController(match.getToolCards()[toolcardIndex], toolcardIndex, client, this::disableNormTcPassUndo));

            try {
                toolcardContainer.getChildren().add(loader.load());
            } catch (IOException e) {
                logger.log(Level.WARNING,e.getMessage());
            }

        }

        // load public objective cards
        for(int i = 0; i < 3; i++){
            final int pubIndex = i;
            loader = new FXMLLoader(getClass().getResource("/CardGUI.fxml"));
            loader.setControllerFactory(c -> new CardController(match.getPublicObjCards()[pubIndex]));
            try {
                publicObjContainer.getChildren().add(loader.load());
            } catch(IOException e){
                logger.log(Level.WARNING,e.getMessage());
            }
        }

        disableNormTcPassUndo();

        undoBtn.setOnMouseClicked(e -> {
            draftPoolController.disableAll();
            roundTrackerController.disableAll();
            playerControllers.get(client.getUsername()).disableAll();
            client.sendUndoRequest();
        });

        publicObjContainer.getChildren().add(timerBar);

    }

    /**
     * Reset in-game timer display
     */
    void resetTimer() {
        timer.reset();
    }
}
