package it.polimi.se2018.controller;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;
import it.polimi.se2018.network.server.Lobby;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class Controller {
    private GameManager gameManager;

    public Controller(Lobby lobby) throws IOException {
        this.gameManager = new GameManager(lobby);
    }

    public void handleMove(PlayerMove move) {
        boolean matchEnded = gameManager.handleMove(move);
        if(matchEnded)
            gameManager.calculateScore();
        //TODO
    }
}
