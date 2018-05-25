package it.polimi.se2018.controller;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;

import java.util.Observable;
import java.util.Observer;

public class Controller implements Observer{
    //private Match match;
    private GameManager gameManager;

    public Controller() {
        //this.match = match;
        this.gameManager = new GameManager();
    }

    public void update(Observable o, Object move) {
        if(gameManager.handleMove((PlayerMove)move))
            gameManager.calculateScore();
        //TODO
    }
}
