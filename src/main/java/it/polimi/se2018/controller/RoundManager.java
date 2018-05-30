package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

class RoundManager {
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
        int lastDieIndex = match.getDraftPool().getCurrentSize() - 1;
        Die lastDieDraftpool = match.getDraftPool().getDice().get(lastDieIndex);

        match.getRoundTracker().insert(lastDieDraftpool);

        newQueue();

        match.setDraftPool(newDraftpool());
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
    private void newQueue(){
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
        match.setPlayerQueue(playerQueue);
    }

    /**
     * Handle the player's move through TurnManager and checks if the round is finished or not.
     * @param playerMove of the player
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

    /**
     * Activate one of the 3 toolcards.
     * Checks if it's possible to use the toolcard and if the player has enough tokens. Increase the cost of the toolcard if activated.
     * @param toolCardId index of toolcard selected
     * @return true if successfully activated
     */
    boolean activateToolcard(int toolCardId) {
        return turnManager.activateToolcard(toolCardId);
    }
}
