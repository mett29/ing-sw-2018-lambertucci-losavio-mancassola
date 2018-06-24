package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class RoundManager {
    private Match match;
    private TurnManager turnManager;

    RoundManager(Match match) {
        this.match = match;
        turnManager = new TurnManager(match);
        this.match.setPlayerQueue(newQueue());
        this.match.setDraftPool(newDraftpool());
        turnManager.newTurn(this.match.getPlayerQueue().peek());
    }

    /**
     * Create new round.
     */
    void newRound() {
        Die lastDieDraftpool = match.getDraftPool().getLastDie();

        //Insert last die from Draft Pool to Round Tracker
        match.getRoundTracker().insert(lastDieDraftpool);

        //Initializes new player queue (as specified by rules)
        match.setPlayerQueue(newQueue());

        //Extracts new DraftPool
        match.setDraftPool(newDraftpool());

        turnManager.newTurn(match.getPlayerQueue().peek());
    }

    /**
     * @return a new draftpool based on players size
     */
    private DiceContainer newDraftpool() {
        int draftpoolSize = match.getPlayers().size()*2 + 1;
        DiceContainer tmpDraftpool = new DiceContainer(draftpoolSize);
        for(int i = 0; i < draftpoolSize; i++)
            tmpDraftpool.insert(match.extractDie());
        return tmpDraftpool;
    }

    /**
     * Creates a new round passing the first turn to the player next to him (clockwise procedure).
     */
    private Queue<Player> newQueue(){
        Queue<Player> playerQueue = new LinkedList<>();

        List<Player> playerList = match.getPlayers();
        int turnNumber = match.getRoundTracker().getCurrentSize();
        int playerSize = playerList.size();
        Queue<Integer> playerIndexes = new LinkedList<>();

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
        return playerQueue;
    }

    /**
     * Handle the player's move.
     * @param playerMove of the player
     * @return true if the all moves are finished, false otherwise
     */
    boolean handleMove(PlayerMove playerMove) {
        return turnManager.handleMove(playerMove);
    }

    /**
     * Activate one of the 3 toolcards.
     * Checks if it's possible to use the toolcard and if the player has enough tokens. Increase the cost of the toolcard if activated.
     * @param username of the player
     * @param toolCardId index of toolcard selected
     * @return true if successfully activated
     */
    boolean activateToolcard(String username, int toolCardId) {
        return turnManager.activateToolcard(username, toolCardId);
    }

    /**
     * Activate pick_die move
     * Checks if it's possible to make a move.
     * @param username of the player
     * @return true if successfully activated
     */
    boolean activateNormalMove(String username) {
        return turnManager.activateNormalMove(username);
    }

    /**
     * Pass the current player's turn to the next one of the queue.
     * Checks if it's possible to pass the turn.
     * Checks if the round if finished.
     * @param username of the player
     * @return true if successfully passed the turn
     */
    boolean passTurn(String username) {
        if(turnManager.passTurn(username)) {
            match.getPlayerQueue().poll();
            if(!match.getPlayerQueue().isEmpty()) {
                turnManager.newTurn(match.getPlayerQueue().peek());
            } else {
                return true;
            }
        }
        return  false;
    }

    /**
     * Undo the current operation
     * @param username of the current player
     * @return true if the state is not YOUR_TURN, false otherwise
     */
    boolean undo(String username) {
        return turnManager.undo(username);
    }
}
