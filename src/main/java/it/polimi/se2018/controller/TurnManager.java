package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TurnManager {
    private Match match;
    private ToolCardController toolcard;
    private List<DieCoord> memory;

    TurnManager(Match match) {
        this.match = match;
        this.toolcard = null;
        this.memory = new ArrayList<>();
    }

    /**
     * Creates a new turn and delegates it to the next player
     * Resets the ArrayList 'memory' for the new player
     * Sets the player state as 'PICK' on 'DRAFTPOOL'
     * @param player to delegate
     */
    void newTurn(Player player) {
        memory = new ArrayList<>();
        player.setState(new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
    }

    /**
     * Check if the player already picked a die (based on his player state)
     * @param state of the player
     * @return true if picked, false if not
     */
    private boolean checkDiePicked(PlayerState state) {
        return state.get() == EnumState.PICK;
    }

    /**
     * Check if the player is on his first or second turn
     * @param playerQueue of the match
     * @return true if it's on his 1st turn, false if it's on his 2nd turn
     */
    private boolean checkTurn(ArrayList<Player> playerQueue) {
        for(int i = 1; i < match.getPlayerQueue().size(); i++) {
            if(match.getPlayerQueue().get(i) == match.getPlayerQueue().get(0))
                return true;
        }
        return false;
    }

    /**
     * Handles the basic move of the player: picking a Die from the draftpool.
     * Checks if there is any intention of using a toolcard, otherwise the handleMove delegates the action to ToolCardController.
     * Before delegating the handleMove to ToolCardController there will be some checks based on the toolcard selected.
     * ------------------------
     * There will be two handleMoves:
     *  1) Picking a Die from the draftpool
     *  2) Selecting the Cell where to put the die in
     * @param playerMove of the player
     * @return true if the state is 'IDLE', false if not
     */
    boolean handleMove(PlayerMove playerMove) {
        PlayerState newState;

        //if(playerMove.isToolcardActivation()){
            // Se il movimento è "attivo la toolcard", attivo la toolcard:
            // - Imposto this.toolcard = new ToolCardController(tc attivata)
        //}

        if(toolcard != null){
            newState = toolcard.handleMove(playerMove);
            if(newState.get() == EnumState.IDLE){
                // deactivate toolcard
                toolcard = null;
            }
        } else {
            DieCoord coord = (DieCoord) playerMove.getMove();
            if(memory.isEmpty()){
                memory.add(coord);
                newState = new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY, CellState.NEAR));
            } else {
                memory.add(coord);
                DieCoord[] coords = (DieCoord[]) (memory.toArray());
                if (isMoveDieMovement(coords)) {
                    throw new InvalidParameterException("playerMove must contain DieCoord[2]");
                }
                Action move = new Switch(coords[0], coords[1]);
                PlacementError err = move.check();
                if (!err.hasNoErrorExceptEdge()) {
                    newState = new PlayerState(EnumState.REPEAT);
                } else {
                    move.perform();
                    newState = new PlayerState(EnumState.IDLE);
                }
            }
        }

        playerMove.getActor().setState(newState);
        return newState.get() == EnumState.IDLE;
    }

    /**
     * Checks if the length of DieCoords is 2
     * @param dc array of DieCoord
     * @return true if the length is 2, false otherwise
     */
    private static boolean isMoveDieMovement(DieCoord[] dc){
        return dc.length != 2;
    }
}
