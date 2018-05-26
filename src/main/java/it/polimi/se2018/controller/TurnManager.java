package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

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
     * Checks if the length of DieCoords is 2
     * @param dc array of DieCoord
     * @return true if the length is 2, false otherwise
     */
    private static boolean isMoveDieMovement(DieCoord[] dc){
        return dc.length != 2;
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

        if(playerMove.getMove() == ToolCard.class) {
            if(Checks.get(((ToolCard)playerMove.getMove()).getId()).apply(match, playerMove)) {
                this.toolcard = new ToolCardController(match, (ToolCard) playerMove.getMove());
                playerMove.getActor().setActivatedToolcard((ToolCard) playerMove.getMove());
            } else {
                newState = new PlayerState(EnumState.REPEAT);
                playerMove.getActor().setState(newState);
                return false;
            }
        }

        if (toolcard != null && playerMove.getActor().getActivatedToolcard() != null) {
            newState = toolcard.handleMove(playerMove);
            if (newState.get() == EnumState.IDLE) {
                toolcard = null;
                playerMove.getActor().setActivatedToolcard(null);
            }
        } else {
            DieCoord coord = (DieCoord) playerMove.getMove();
            if (memory.isEmpty()) {
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
     * A static class where belongs all checks of every toolcard.
     */
    private static class Checks {
        static final Map<Integer, BiFunction<Match, PlayerMove, Boolean>> checks;

        /**
         * Check if the player already picked a die (based on his player state)
         * @param state of the player
         * @return true if picked, false if not
         */
        private static boolean checkDiePicked(PlayerMove state) {
            return state.getActor().getState().get() == EnumState.PICK;
        }

        /**
         * Check if the player is on his first or second turn
         * @param match of the game
         * @return true if it's on his 1st turn, false if it's on his 2nd turn
         */
        private static boolean checkTurn(Match match) {
            for(int i = 1; i < match.getPlayerQueue().size(); i++) {
                if(match.getPlayerQueue().get(i) == match.getPlayerQueue().get(0))
                    return true;
            }
            return false;
        }

        /**
         * Check if there are any die on player's board
         * @param move of the player
         * @param number of dice on board
         * @return true if dice on board exceed the number, false otherwise
         */
        private static boolean checkDiceOnBoard(PlayerMove move, int number) {
            return move.getActor().getBoard().countDices() >= number;
        }

        /**
         * Check if there are any die on match's roundtracker
         * @param match of the game
         * @param number of dice on roundtracker
         * @return true if dice on roundtracker exceed the number, false otherwise
         */
        private static boolean checkDiceOnRoundtracker(Match match, int number) {
            return match.getRoundTracker().getCurrentSize() >= number;
        }

        static {
            Map<Integer, BiFunction<Match, PlayerMove, Boolean>> tmpChecks = new HashMap<>();

            BiFunction<Match, PlayerMove, Boolean> tc0 = (match, playerMove) -> true;

            tmpChecks.put(0, tc0);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc1 = (match, playerMove) -> checkDiceOnBoard(playerMove, 1);

            tmpChecks.put(1, tc1);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc2 = (match, playerMove) -> checkDiceOnBoard(playerMove, 1);

            tmpChecks.put(2, tc2);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc3 = (match, playerMove) -> checkDiceOnBoard(playerMove, 2);

            tmpChecks.put(3, tc3);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc4 = (match, playerMove) -> checkDiceOnRoundtracker(match, 1);

            tmpChecks.put(4, tc4);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc5 = (match, playerMove) -> true;

            tmpChecks.put(5, tc5);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc6 = (match, playerMove) -> !checkTurn(match) && !checkDiePicked(playerMove);

            tmpChecks.put(6, tc6);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc7 = (match, playerMove) -> checkTurn(match);

            tmpChecks.put(7, tc7);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc8 = (match, playerMove) -> true;

            tmpChecks.put(8, tc8);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc9 = (match, playerMove) -> true;

            tmpChecks.put(9, tc9);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc10 = (match, playerMove) -> true;

            tmpChecks.put(10, tc10);

            //-----------------------------------------------------------

            BiFunction<Match, PlayerMove, Boolean> tc11 = (match, playerMove) -> checkDiceOnBoard(playerMove, 2);

            tmpChecks.put(11, tc11);

            checks = Collections.unmodifiableMap(tmpChecks);
        }

        static BiFunction<Match, PlayerMove, Boolean> get(int mapIndex){
            return checks.get(mapIndex);
        }
    }
}
