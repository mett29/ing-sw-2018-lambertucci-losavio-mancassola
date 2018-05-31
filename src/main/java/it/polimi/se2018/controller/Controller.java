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
     * Handle player's move. If the match is ended, it calculates the final score.
     * @param move of the player
     */
    public void handleMove(PlayerMove move) {
        boolean matchEnded = gameManager.handleMove(move);
        if(matchEnded)
            gameManager.calculateScore();
        //TODO
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
        return gameManager.activateToolcard(username, toolCardId);
    }

    /**
     * @return the 4 extracted patterns between which the player will chose
     */
    public List<ParsedBoard> extractPatterns() {
        return this.gameManager.extractPatterns();
    }

    public Match getMatch() {
        return gameManager.getMatch();
    }
}
