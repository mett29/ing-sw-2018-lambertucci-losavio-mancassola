package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX controller of the Pattern-Pick phase
 */
public class PatternPickController {
    private final Client client;
    private PrivateObjCard privateObjCard;
    @FXML
    private GridPane boardGrid;
    @FXML
    private VBox playerInfos;

    private List<Board> boards;
    private List<String> boardNames;

    /**
     * Constructor
     * @param boards List of pattern to chose from
     * @param boardNames List of names of the patterns
     * @param client Reference to the Client object
     * @param privateObjCard Private Objective Card of the player
     */
    PatternPickController(List<Board> boards, List<String> boardNames, Client client, PrivateObjCard privateObjCard) {
        this.boards = boards;
        this.client = client;
        this.privateObjCard = privateObjCard;
        this.boardNames = boardNames;
    }

    @FXML
    public void initialize(){
        Logger logger = Logger.getLogger("patternPickController");

        // Add player infos
        playerInfos.getChildren().add(new Label(client.getUsername()));

        Color privateColor = privateObjCard.getColor();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CellGUI.fxml"));
        loader.setControllerFactory(c -> new CellController(new Cell(new Restriction(privateColor)), false));
        try {
            playerInfos.getChildren().add(new Label("Obiettivo privato:"));
            playerInfos.getChildren().add(loader.load());
        } catch(IOException e){
            logger.log(Level.WARNING,e.getMessage());
        }


        // Add boards
        for (int i = 0; i < boards.size(); i++){
            Board board = boards.get(i);
            String name = boardNames.get(i);
            try {
                loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/BoardGUI.fxml"));
                loader.setControllerFactory(c -> new BoardGUIController(board, client));
                Parent node = loader.load();


                BorderPane pane = new BorderPane();
                HBox header = new HBox();

                header.setSpacing(5);
                header.getChildren().add(new Label(name));
                header.getChildren().add(new Label("Difficoltà: " + board.getBoardDifficulty()));

                Button selectBoard = new Button("Select");

                final int constantIndex = i;
                selectBoard.setOnMouseClicked(event -> {
                    boardGrid.setDisable(true);
                    client.sendPatternResponse(constantIndex);
                });

                header.getChildren().add(selectBoard);

                pane.setTop(header);
                pane.setCenter(node);
                boardGrid.add(pane, i % 2, i / 2);
            } catch(IOException e){
                logger.log(Level.WARNING,e.getMessage());
            }
        }

        boardGrid.setGridLinesVisible(true);
    }
}
