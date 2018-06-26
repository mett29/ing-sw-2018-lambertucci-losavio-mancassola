package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Cell;
import it.polimi.se2018.model.CellState;
import it.polimi.se2018.model.DiceContainer;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.client.DiceContainerCoordMove;
import it.polimi.se2018.view.CLI.CLI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DiceContainerController {
    private final Client client;
    private final DiceContainerCoordMove.DiceContainerName name;
    private List<CellController> cellControllers;
    @FXML
    private AnchorPane container;

    private DiceContainer diceContainer;
    private GridPane gridPane;

    public DiceContainerController(DiceContainer diceContainer, DiceContainerCoordMove.DiceContainerName name, Client client) {
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
            loader.setControllerFactory(c -> new CellController(cell, false, name, new int[]{index}));

            try {
                gridPane.addColumn(i, ((Node) loader.load()));
                cellControllers.add(loader.getController());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void initialize(){
        gridPane.setPrefSize(30, 30);
        container.getChildren().add(gridPane);
        container.setPrefSize(30 * diceContainer.getMaxSize(), 30);
    }

    public void setDisabled(boolean disabled){
        for(int i = 0; i < diceContainer.getMaxSize(); i++){
            cellControllers.get(i).setDisabled(disabled);
        }
    }

    public void update(DiceContainer diceContainer){
        for(int i = 0; i < diceContainer.getMaxSize(); i++){
            cellControllers.get(i).update(diceContainer.getCell(i));
        }

        this.diceContainer = diceContainer;
    }

    void activate(EnumSet<CellState> cellStates) {
        DiceContainerController toDisable = this;
        for (int i = 0; i < diceContainer.getMaxSize(); i++) {
            final int index = i;

            if(CLI.Stringifier.acceptedCell(diceContainer, i, cellStates)) {
                cellControllers.get(i).activate(e -> {
                    toDisable.disableAll();
                    client.sendMove(new DiceContainerCoordMove(index, name));
                });
            } else {
                cellControllers.get(i).disable();
            }
        }
    }

    public void disableAll() {
        for (int i = 0; i < diceContainer.getMaxSize(); i++) {
            cellControllers.get(i).disable();
        }
    }
}
