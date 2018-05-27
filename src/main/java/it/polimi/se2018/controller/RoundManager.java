package it.polimi.se2018.controller;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class RoundManager {
    private Match match;
    private TurnManager turnManager;

    RoundManager(Match match) {
        this.match = match;
        turnManager = new TurnManager(match);
    }

    /**
     * Create new round.
     * Insert last die from Draft Pool to Round Tracker
     * Initializes new player queue (as specified by rules)
     * Extracts new DraftPool
     */
    void newRound() {
        //TODO: last die from draft pool to round tracker
        newQueue();
        //TODO: extract new draft pool
    }

    /**
     * Creates a new round passing the first turn to the player next to him (clockwise procedure).
     */
    void newQueue(){
        Queue<Player> playerQueue = new PriorityQueue<>();

        List<Player> playerList = match.getPlayers();
        int turnNumber = match.getRoundTracker().getCurrentSize();
        int playerSize = playerList.size();
        Queue<Integer> playerIndexes = new PriorityQueue<>();

        for(int i = 0; i < playerSize; i++){
            playerIndexes.add((i + turnNumber) % playerSize);
        }

        Integer[] indexes = playerIndexes.toArray(new Integer[playerSize]);

        for(int i = 0; i < 2 * playerIndexes.size(); i++){
            if(i < playerIndexes.size()){
                playerQueue.add(playerList.get(indexes[i]));
            } else {
                playerQueue.add(playerList.get(indexes[2 * playerSize - i - 1]));
            }
        }
        match.setPlayerQueue(playerQueue);
    }

    /**
     * Handle the player's move through TurnManager and checks if the round is finished or not.
     * @param playerMove
     * @return true if the round is finished, false otherwise
     */
    boolean handleMove(PlayerMove playerMove) {
        if(turnManager.handleMove(playerMove)) {
            if(match.getPlayerQueue().isEmpty()){
                return true;
            } else {
                turnManager.newTurn(match.getPlayerQueue().poll());
                return false;
            }
        } else {
            return false;
        }
    }
}
