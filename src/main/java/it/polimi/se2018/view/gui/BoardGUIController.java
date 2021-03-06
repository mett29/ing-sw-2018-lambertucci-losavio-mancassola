package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.Cell;
import it.polimi.se2018.model.CellState;
import it.polimi.se2018.network.client.BoardCoordMove;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.view.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX controller of the Board
 */
public class BoardGUIController {
    private final Client client;
    @FXML
    private GridPane gridPane;

    private CellController[][] controllerMatrix;

    private Board board;

    /**
     * Constructor
     * @param board Board to display
     * @param client Reference to the client object
     */
    BoardGUIController(Board board, Client client){
        this.board = board;
        this.client = client;
        controllerMatrix = new CellController[4][5];
    }

    @FXML
    public void initialize(){
        gridPane.setGridLinesVisible(true);
        for(int y = 0; y < 4; y++){
            for(int x = 0; x < 5; x ++){
                final Cell cell = board.getCell(x, y);
                final int x_coord = x;
                final int y_coord = y;
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/CellGUI.fxml"));
                loader.setControllerFactory(c -> new CellController(cell, false, new int[]{x_coord, y_coord}));

                try {
                    gridPane.add(loader.load(), x, 3 - y);
                    controllerMatrix[y][x] = loader.getController();
                } catch (IOException e) {
                    Logger.getLogger("boardGuiController").log(Level.WARNING,e.getMessage());
                }
            }
        }
    }


    /**
     * Update content of the board
     * @param board New board state to display
     */
    void update(Board board) {
        for(int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                controllerMatrix[y][x].update(board.getCell(x, y));
            }
        }

        this.board = board;
    }

    /**
     * Activate board cells that respect the state described in cellStates
     * @param cellStates Set of accepted cell states
     */
    void activate(Set<CellState> cellStates) {
        final BoardGUIController toDisable = this;
        for(int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                if(Utils.acceptedCell(board, x, y, cellStates)) {
                    final int x_fin = x;
                    final int y_fin = y;
                    controllerMatrix[y][x].activate(e -> {
                        toDisable.disableAll();
                        client.sendMove(new BoardCoordMove(x_fin, y_fin));
                    });
                } else {
                    controllerMatrix[y][x].disable();
                }
            }
        }
    }

    /**
     * Disable each cell
     */
    void disableAll() {
        for(int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                controllerMatrix[y][x].disable();
            }
        }
    }
}
