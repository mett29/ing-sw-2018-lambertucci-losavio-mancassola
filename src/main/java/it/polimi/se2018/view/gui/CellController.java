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
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CellController {
    private final boolean active;
    @FXML
    private AnchorPane anchorPane;

    private Cell cell;
    private Callback<ClientMove, Void> onClick;

    private Button die;
    private ClientMove move;


    /**
     * Constructor
     * To be used when displaying a cell in a board
     * @param cell        Cell to be displayed
     * @param active      If true, the cell will have a clickable button
     * @param coordinates The coordinates of the cell to be displayed (length must be 2)
     */
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

    /**
     * Constructor
     * @param cell   The cell to be displayed
     * @param active If true, the cell will have a clickable button
     */
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

    /**
     * Constructor
     * To be used when displaying a cell in a DiceContainer
     * @param cell        Reference to the cell to be displayed
     * @param active      If true, the cell will have a clickable button
     * @param name        Type of container (RoundTracker or DraftPool)
     * @param index       Index of the cell to be displayed
     */
    CellController(Cell cell, boolean active, DiceContainerCoordMove.DiceContainerName name, int index){
        this(cell, active);
        move = new DiceContainerCoordMove(index, name);
    }

    /**
     * Disable component
     * @param disabled If true, disable component
     */
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

        die.setVisible(active);

        anchorPane.setDisable(!active);

    }

    /**
     * Set background style of the cell with respect to the restriction
     * @param cell        Reference to the cell whose background is to be set
     * @param restriction Restriction to be displayed
     */
    private void setCellStyle(AnchorPane cell, Restriction restriction){
        String sRestriction = restriction.toString();
        String style = "-fx-background-position: center; -fx-background-repeat: no-repeat; -fx-background-size: 100%; " + colorMap.get(sRestriction);
        cell.setStyle(style);
    }

    /**
     * Set text of the die button
     * @param text String to be set
     */
    private void setDieText(String text){
        Platform.runLater(() -> die.setText(text));
    }

    /**
     * Update content of the cell
     * @param cell New cell state
     */
    void update(Cell cell) {
        if (cell.getDie() == null) {
            setDieText("");
            die.setVisible(false);
        } else {
            die.setVisible(true);
            setDieText(String.valueOf(cell.getDie().getValue()));
            die.setStyle(colorStyleMap.get(cell.getDie().getColor()));
        }
    }

    /**
     * Activate the cell button
     * @param eventHandler Function that will be executed when the cell button will be clicked
     */
    void activate(EventHandler<? super MouseEvent> eventHandler) {
        anchorPane.setDisable(false);
        die.setVisible(true);
        die.setOnMouseClicked(eventHandler);
    }

    /**
     * Disable cell
     */
    void disable(){
        anchorPane.setDisable(true);
    }

    private static final Map<String, String> colorMap;
    static {
        Map<String, String> tmpMap = new HashMap<>();
        tmpMap.put("r", "-fx-background-color: red");
        tmpMap.put("b", "-fx-background-color: blue");
        tmpMap.put("y", "-fx-background-color: yellow");
        tmpMap.put("p", "-fx-background-color: purple");
        tmpMap.put("g", "-fx-background-color: green");
        tmpMap.put("1", "-fx-background-image: url('/img/1.png')");
        tmpMap.put("2", "-fx-background-image: url('/img/2.png')");
        tmpMap.put("3", "-fx-background-image: url('/img/3.png')");
        tmpMap.put("4", "-fx-background-image: url('/img/4.png')");
        tmpMap.put("5", "-fx-background-image: url('/img/5.png')");
        tmpMap.put("6", "-fx-background-image: url('/img/6.png')");

        colorMap = Collections.unmodifiableMap(tmpMap);

    }
}
