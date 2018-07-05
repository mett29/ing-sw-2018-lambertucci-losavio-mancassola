package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.Client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Set;

public class PlayerController {
    private final boolean isMe;
    private final Client client;



    private Player player;
    private BoardGUIController boardController;
    private CellController pickedController;

    /**
     * Constructor
     * @param player Reference to the player to be displayed
     * @param isMe   If true, private object card will be shown
     * @param client Reference to the client object
     */
    PlayerController(Player player, boolean isMe, Client client){
        this.player = player;
        this.isMe = isMe;
        this.client = client;
    }

    /**
     * Update content of the section
     * @param player Reference to the player to be displayed
     */
    void update(Player player){
        this.player = player;
        Platform.runLater(() -> playerName.setText(player.getName() + (player.isDisconnected() ? " (DISCONN.)" : "")));
        Platform.runLater(() -> tokens.setText("Tokens: " + player.getToken()));

        Board board = player.getBoard();
        boardController.update(board);
        updatePicked(player.getPickedDie());
    }

    @FXML
    private VBox playerInfoContainer;
    @FXML
    private Label playerName;

    private Label tokens;
    @FXML
    private BorderPane borderPane;

    @FXML
    public void initialize(){
        Board board = player.getBoard();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/BoardGUI.fxml"));
        loader.setControllerFactory(c -> new BoardGUIController(board, client));

        try {
            borderPane.setCenter(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boardController = loader.getController();

        playerName.setText(player.getName() + (player.isDisconnected() ? " (DISCONN.)" : ""));

        tokens = new Label(("Tokens: " + player.getToken()));
        playerInfoContainer.getChildren().add(tokens);

        // deactivate pane if it isn't "mine"
        borderPane.setDisable(!isMe);


        loader = new FXMLLoader(getClass().getResource("/CellGUI.fxml"));
        loader.setControllerFactory(e -> new CellController(new Cell(null), false));
        try {
            playerInfoContainer.getChildren().add(loader.load());
            pickedController = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display private object card color
        if(isMe){
            Color privateColor = player.getPrivateObjCard().getColor();
            loader = new FXMLLoader(getClass().getResource("/CellGUI.fxml"));
            loader.setControllerFactory(c -> new CellController(new Cell(new Restriction(privateColor)), false));
            try {
                playerInfoContainer.getChildren().add(new Label("Obiettivo privato:"));
                playerInfoContainer.getChildren().add(loader.load());
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Update picked die
     * @param die Die to be displayed in PickedDie section
     */
    private void updatePicked(Die die) {
        Cell pp = new Cell(null);
        pp.setDie(die);

        pickedController.update(pp);
    }

    /**
     * Activate conditionally each cell of the player's board
     * @param cellStates Set of conditions for a cell to be active
     */
    void activate(Set<CellState> cellStates) {
        boardController.activate(cellStates);
    }

    /**
     * Disable each cell of the player's board
     */
    void disableAll(){
        boardController.disableAll();
    }
}
