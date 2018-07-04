package it.polimi.se2018.view.gui;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.client.BoardCoordMove;
import it.polimi.se2018.network.client.ClientMove;
import it.polimi.se2018.network.client.DiceContainerCoordMove;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.security.InvalidParameterException;
import java.util.EnumMap;
import java.util.Map;

public class CellController {
    private final boolean active;
    @FXML
    private AnchorPane anchorPane;

    private Cell cell;
    private Callback<ClientMove, Void> onClick;

    private Button die;
    private ClientMove move;


    CellController(Cell cell, boolean active, int[] coordinates){
        this(cell, active);

        if(coordinates.length != 2){
            // this is a DiceContainer cell
            throw new InvalidParameterException("`coordinates.length` must be 2 to construct board's cells.\n" +
                    "If you want to construct dice container's cells, call `CellController(cell, active, DiceContainerName, coords)`");
        } else {
            // this is a board cell
            move = new BoardCoordMove(coordinates[0], coordinates[1]);
        }
    }

    CellController(Cell cell, boolean active) {
        colorStyleMap = new EnumMap<>(Color.class);
        colorStyleMap.put(Color.RED, "-fx-background-color: red");
        colorStyleMap.put(Color.BLUE, "-fx-background-color: blue");
        colorStyleMap.put(Color.GREEN, "-fx-background-color: green");
        colorStyleMap.put(Color.PURPLE, "-fx-background-color: purple");
        colorStyleMap.put(Color.YELLOW, "-fx-background-color: yellow");


        this.cell = cell;
        this.active = active;
        this.onClick = null;

        this.die = new Button();

        this.die.setId("die-button");

        if(cell.getDie() != null){
            this.die.setText(String.valueOf(cell.getDie().getValue()));
            this.die.setStyle(colorStyleMap.get(cell.getDie().getColor()));
        }
    }

    private final Map<Color, String> colorStyleMap;

    CellController(Cell cell, boolean active, DiceContainerCoordMove.DiceContainerName name, int[] coordinates){
        this(cell, active);

        if(coordinates.length != 1){
            throw new InvalidParameterException("`coordinates.length` must be 1 to construct dice container's cells.\n" +
                    "If you want to construct board's cells, call `CellController(cell, active, coords)`");
        } else {
            move = new DiceContainerCoordMove(coordinates[0], name);
        }
    }


    public void setOnClick(Callback<ClientMove, Void> onClick) {
        this.onClick = onClick;
    }

    public void setDisabled(boolean disabled){
        die.setDisable(disabled);
    }

    public void initialize(){
        anchorPane.setStyle("-fx-background-size: cover, auto; -fx-background-repeat: no-repeat; -fx-background-position: center");
        if(cell.getRestriction() != null){
            setCellStyle(anchorPane, cell.getRestriction());
        }

        if(onClick != null) {
            this.die.setOnMouseClicked(e -> onClick.call(move));
        }

        anchorPane.getChildren().add(this.die);

        anchorPane.setDisable(!active);

    }

    private void setCellStyle(AnchorPane cell, Restriction restriction){
        String sRestriction = restriction.toString();
        switch(sRestriction){
            case "r":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/red.png')");
                break;
            case "b":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/blue.png')");
                break;
            case "y":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/yellow.png')");
                break;
            case "p":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/purple.png')");
                break;
            case "g":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/green.png')");
                break;
            case "1":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/1.png')");
                break;
            case "2":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/2.png')");
                break;
            case "3":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/3.png')");
                break;
            case "4":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/4.png')");
                break;
            case "5":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/5.png')");
                break;
            case "6":
                cell.setStyle("-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; -fx-background-image: url('/img/6.png')");
                break;
            default:
                // do nothing
        }
    }

    private void setDieText(String text){
        Platform.runLater(() -> die.setText(text));
    }

    void update(Cell cell) {
        if (cell.getDie() == null) {
            setDieText("");
            die.setStyle("-fx-background-color: white");
        } else {
            setDieText(String.valueOf(cell.getDie().getValue()));
            die.setStyle(colorStyleMap.get(cell.getDie().getColor()));
        }
    }

    void activate(EventHandler<? super MouseEvent> eventHandler) {
        anchorPane.setDisable(false);
        die.setOnMouseClicked(eventHandler);
    }

    void disable(){
        anchorPane.setDisable(true);
    }
}
