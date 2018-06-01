package it.polimi.se2018.view.GUI;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.network.client.Client;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class MatchController {
    public GridPane boardGrid;
    private Client client;

    public void setClient(Client client) {
        this.client = client;
    }

    private Map<String, PlayerController> controllers;
    private Map<String, AnchorPane> panes;

    public MatchController(Match match){
        panes = new HashMap<>();

        controllers =  match.getPlayers().stream().collect(Collectors.toMap(Player::getName, PlayerController::new));
        for (Map.Entry<String, PlayerController> player : controllers.entrySet()) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/PlayerGUI.fxml"));
            loader.setController(player.getValue());
            try {
                AnchorPane playerGui = loader.load();
                panes.put(player.getKey(), playerGui);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(Match match){
        //TODO
    }

    @FXML
    public void initialize(){
        for (AnchorPane pane : panes.values()) {
            boardGrid.add(pane, 0, 0);
        }

    }
}
