package it.polimi.se2018.controller;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;
import it.polimi.se2018.network.server.Lobby;
import it.polimi.se2018.network.server.ParsedBoard;

import java.io.IOException;
import java.util.List;

public class Controller {
    private GameManager gameManager;

    public Controller(Lobby lobby) throws IOException {
        this.gameManager = new GameManager(lobby);
    }

    /**
     * Handle player's move.
     * @param move of the player
     */
    public /*boolean*/ void handleMove(PlayerMove move) {
        /*return*/ gameManager.handleMove(move);
        getMatch().notifyObservers();
    }

    /**
     * Activate one of the 3 toolcards.
     * Checks if the username coincide with the first player's name of the queue
     * Checks if it's possible to use the toolcard and if the player has enough tokens. Increase the cost of the toolcard if activated.
     * @param username of the player
     * @param toolCardId index of toolcard selected
     * @return true if successfully activated
     */
    public boolean activateToolcard(String username, int toolCardId) {
        boolean ret =  gameManager.activateToolcard(username, toolCardId);
        getMatch().notifyObservers();
        return ret;
    }

    /**
     * Activate pick_die move
     * Checks if it's possible to make a move.
     * @param username of the player
     * @return true if successfully activated
     */
    public boolean activateNormalMove(String username) {
        boolean ret = gameManager.activateNormalMove(username);
        getMatch().notifyObservers();
        return ret;
    }

    /**
     * Pass the current player's turn
     * Checks if it's possible to pass the turn
     * @param username of the player
     * @return true if successfully passed the turn
     */
    public void passTurn(String username) {
        boolean matchEnded = gameManager.passTurn(username);
        if(matchEnded)
            gameManager.calculateScore();
        getMatch().notifyObservers();
    }

    /**
     * @return the 4 extracted patterns between which the player will chose
     */
    public List<ParsedBoard> extractPatterns() {
        return this.gameManager.extractPatterns();
    }

    /**
     * @return the current match
     */
    public Match getMatch() {
        return gameManager.getMatch();
    }
}
