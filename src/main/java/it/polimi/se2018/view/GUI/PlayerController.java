package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

public class PlayerController {
    private Player player;

    public PlayerController(Player player){
        this.player = player;
    }

    public void update(Player player){
        this.player = player;
        onUpdate();
    }

    private void onUpdate(){
        playerName.setText(player.getName());
        tokens.setProgress(player.getToken() / 10D);
    }

    @FXML
    private Label playerName;
    @FXML
    private ProgressBar tokens;
    @FXML
    private GridPane boardGrid;

    @FXML
    public void initialize(){
        onUpdate();
    }
}
