package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.Cell;
import it.polimi.se2018.model.CellState;
import it.polimi.se2018.model.DiceContainer;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.client.DiceContainerCoordMove;
import it.polimi.se2018.view.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX controller of DiceContainer
 */
public class DiceContainerController {
    private final Client client;
    private final DiceContainerCoordMove.DiceContainerName name;
    private List<CellController> cellControllers;
    @FXML
    private AnchorPane container;

    private DiceContainer diceContainer;
    private GridPane gridPane;

    private static Logger logger = Logger.getLogger("diceContainerController");

    /**
     * Constructor
     * @param diceContainer Reference to the diceContainer to be displayed
     * @param name          Name of the diceContainer (RoundTracker or DraftPool)
     * @param client        Reference to the client object
     */
    DiceContainerController(DiceContainer diceContainer, DiceContainerCoordMove.DiceContainerName name, Client client) {
        this.diceContainer = diceContainer;
        this.client = client;

        this.name = name;

        gridPane = new GridPane();
        final int numCols = diceContainer.getMaxSize();

        cellControllers = new ArrayList<>();

        gridPane.setGridLinesVisible(true);

        for(int i = 0; i < numCols; i++){
            final Cell cell = diceContainer.getCell(i);
            final int index = i;

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/CellGUI.fxml"));
            loader.setControllerFactory(c -> new CellController(cell, false, name, index));

            try {
                gridPane.addColumn(i, ((Node) loader.load()));
                cellControllers.add(loader.getController());
            } catch (IOException e) {
                logger.log(Level.WARNING,e.getMessage());
            }
        }
    }

    public void initialize(){
        gridPane.setPrefSize(30, 30);
        container.getChildren().add(gridPane);
        container.setPrefSize(30 * diceContainer.getMaxSize(), 30);
    }

    /**
     * Disable each cell of the container
     * @param disabled If true, disable each cell, else activate each cell
     */
    public void setDisabled(boolean disabled){
        for(int i = 0; i < diceContainer.getMaxSize(); i++){
            cellControllers.get(i).setDisabled(disabled);
        }
    }

    /**
     * Update content of the diceContainer
     * @param diceContainer New diceContainer state
     */
    void update(DiceContainer diceContainer){
        for(int i = 0; i < diceContainer.getMaxSize(); i++){
            cellControllers.get(i).update(diceContainer.getCell(i));
        }

        this.diceContainer = diceContainer;
    }

    /**
     * Activate diceContainer cells that respect the state described in cellStates
     * @param cellStates Set of accepted cell states
     */
    void activate(Set<CellState> cellStates) {
        DiceContainerController toDisable = this;
        for (int i = 0; i < diceContainer.getMaxSize(); i++) {
            final int index = i;

            if(Utils.acceptedCell(diceContainer, i, cellStates)) {
                cellControllers.get(i).activate(e -> {
                    toDisable.disableAll();
                    client.sendMove(new DiceContainerCoordMove(index, name));
                });
            } else {
                cellControllers.get(i).disable();
            }
        }
    }

    /**
     * Disable each cell of the container
     */
    void disableAll() {
        for (int i = 0; i < diceContainer.getMaxSize(); i++) {
            cellControllers.get(i).disable();
        }
    }
}
