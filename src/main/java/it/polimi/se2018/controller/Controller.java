package it.polimi.se2018.controller;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;
import it.polimi.se2018.network.server.Lobby;

/**
 * This class is the highest level class in Controller package.
 * When a new lobby is born, a new Controller is created.
 * @author ontech7
 */
public class Controller {
    private GameManager gameManager;

    public Controller(Lobby lobby) {
        this.gameManager = new GameManager(lobby);
    }

    /**
     * Handle player's move.
     * @param move of the player
     */
    public void handleMove(PlayerMove move) {
        gameManager.handleMove(move);
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
     */
    public void passTurn(String username) {
        boolean matchEnded = gameManager.passTurn(username);

        if(matchEnded) {
            gameManager.calculateScore();
            gameManager.declareWinner();
        }

        getMatch().notifyObservers();
    }

    /**
     * Undo the current operation
     * @param username of the current player
     * @return true if the state is not YOUR_TURN, false otherwise
     */
    public boolean undo(String username) {
        boolean ret = gameManager.undo(username);
        getMatch().notifyObservers();
        return ret;
    }

    /**
     * @return the current match
     */
    public Match getMatch() {
        return gameManager.getMatch();
    }
}
