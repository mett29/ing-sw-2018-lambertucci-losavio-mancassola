package it.polimi.se2018.controller;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

import java.util.ArrayList;
import java.util.List;

public class RoundManager {
    private Match match;
    TurnManager turnManager;

    RoundManager(Match match) {
        this.match = match;
        turnManager = new TurnManager(match);
    }

    /**
     * Creates a new round passing the first turn to the player next to him (clockwise procedure).
     * @param match that is disputing
     */
    void newRound(Match match) {
        List<Player> playerQueue = new ArrayList<>();

        //TODO: algoritmo dei round

        match.setPlayerQueue(playerQueue);
    }

    /**
     * Handle the player's move through TurnManager and checks if the turn is finished or not.
     * @param playerMove
     * @return true if the turn is finished, false otherwise
     */
    boolean handleMove(PlayerMove playerMove) {
        if(turnManager.handleMove(playerMove)) {
            match.getPlayerQueue().remove(0);
            turnManager.newTurn(match.getPlayerQueue().get(0));
            return true;
        }
        return false;
    }
}
