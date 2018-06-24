package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.function.BiFunction;

class TurnManager {
    private Match match;
    private ToolCardController toolcard;
    private List<DieCoord> memory;

    private Board savedBoard;
    private DiceContainer savedDraftpool;
    private DiceContainer savedRoundtracker;
    private int oldCost;

    TurnManager(Match match) {
        this.match = match;
        this.toolcard = null;
        this.memory = new ArrayList<>();
    }

    /**
     * Creates a new turn and delegates it to the next player
     * @param player to delegate
     */
    void newTurn(Player player) {
        //Resets the ArrayList 'memory' for the new player
        memory = new ArrayList<>();
        //Sets the player state as 'YOUR_TURN'
        player.setState(new PlayerState(EnumState.YOUR_TURN));
        //Sets up all possible actions to the current player
        player.possibleActionsSetUp();
    }

    /**
     * Cancel the current operation and return to the precedent state
     * @param username of the current player
     * @return true if board, draftpool and roundtracker are correctly restored, false otherwise
     */
    boolean cancelOperation(String username) {
        Player currentPlayer = match.getPlayerQueue().peek();

        boolean isPlayerEqual = match.getPlayerByName(username).equals(currentPlayer);

        if(isPlayerEqual) {
            currentPlayer.getBoard().restoreState(savedBoard);
            match.getDraftPool().restoreState(savedDraftpool);
            match.getRoundTracker().restoreState(savedRoundtracker);
            if(toolcard != null) {
                currentPlayer.setToken(currentPlayer.getToken() + oldCost);
                currentPlayer.getActivatedToolcard().setCost(oldCost);
                currentPlayer.deactivateToolcard();
                toolcard = null;
            }
            return true;
        }

        return false;
    }

    /**
     * Undo the current operation
     * @param username of the current player
     * @return true if the state is not YOUR_TURN, false otherwise
     */
    boolean undo(String username) {
        Player currentPlayer = match.getPlayerQueue().peek();

        boolean isPlayerEqual = match.getPlayerByName(username).equals(currentPlayer);

        if(isPlayerEqual && currentPlayer.getState().get() != EnumState.YOUR_TURN) {
            cancelOperation(username);
            currentPlayer.setState(new PlayerState(EnumState.YOUR_TURN));
            return true;
        }

        return false;
    }

    /**
     * Process a move of the player.
     * @param move of the player
     * @return true if the state is 'YOUR_TURN'
     */
    boolean handleMove(PlayerMove move) {
        Player currentPlayer = match.getPlayerQueue().peek();
        PlayerState newState = null;

        boolean isToolcardActive = (currentPlayer.getActivatedToolcard() != null);

        //Checks if there is a toolcard active and the current player has 'ACTIVATE_TOOLCARD' possible action
        if(isToolcardActive && currentPlayer.getPossibleActions().contains(PossibleAction.ACTIVATE_TOOLCARD)) {
            newState = toolcard.handleMove(move);
            if(newState.get() == EnumState.YOUR_TURN) {
                toolcard = null;
                currentPlayer.deactivateToolcard();
            }
        //Checks if the current player's state is not 'YOUR_TURN' and the current player has 'PICK_DIE' possible action
        } else if(currentPlayer.getState().get() != EnumState.YOUR_TURN && currentPlayer.getPossibleActions().contains(PossibleAction.PICK_DIE)) {
            DieCoord coord = (DieCoord) move.getMove();
            //If 'memory' is empty, add the first DieCoord
            if (memory.isEmpty()) {
                memory.add(coord);
                //Sets the new state to 'PICK' on current player's board and he must select only EMPTY cells NEAR dice
                newState = new PickState(EnumSet.of(Component.BOARD), EnumSet.of(CellState.EMPTY, CellState.NEAR));
            } else {
                memory.add(coord);
                if (memory.size() != 2) {
                    throw new InvalidParameterException("playerMove must contain DieCoord[2]");
                }
                //The player decided the cell to put the die inside. Switch action will be performed
                Action moveDice = new Switch(memory.get(0), memory.get(1));
                PlacementError err = moveDice.check();
                int diceOnBoard = currentPlayer.getBoard().countDice();
                //Checks if the board is empty or not. The player's first die must be positioned on an EDGE.
                if ((diceOnBoard == 0 && err.hasErrorFilter(EnumSet.of(Flags.NEIGHBOURS))) || (diceOnBoard != 0 && !err.hasNoErrorExceptEdge())) {
                    newState = new PlayerState(EnumState.REPEAT);
                    memory.remove(1);
                } else {
                    //Perform the action if all conditions are met
                    moveDice.perform();
                    //Remove 'PICK_DIE' possible action from possibleActions of the current player, so he can't do this action anymore on the current turn
                    currentPlayer.possibleActionsRemove(PossibleAction.PICK_DIE);
                    //Actions finished, the new state is 'YOUR_TURN'
                    newState = new PlayerState(EnumState.YOUR_TURN);
                }
            }
        }

        currentPlayer.setState(newState);
        return currentPlayer.getState().get() == EnumState.YOUR_TURN;
    }

    /**
     * Activate pick_die move
     * @param username of the player
     * @return true if successfully activated
     */
    boolean activateNormalMove(String username) {
        Player currentPlayer = match.getPlayerQueue().peek();

        boolean isPlayerEqual = match.getPlayerByName(username).equals(currentPlayer);
        boolean isYourTurnState = currentPlayer.getState().get() == EnumState.YOUR_TURN;

        //Checks if the player's name coincides with the current player of the queue, if he is in 'YOUR_TURN' state and if it contains 'PICK_DIE' possible action
        if(isPlayerEqual && isYourTurnState && currentPlayer.getPossibleActions().contains(PossibleAction.PICK_DIE)) {

            savedBoard = currentPlayer.getBoard().saveState();
            savedDraftpool = match.getDraftPool().saveState();
            savedRoundtracker = match.getRoundTracker().saveState();

            //His state it's 'PICK' on DRAFTPOOL and he must select a FULL cell
            currentPlayer.setState(new PickState(EnumSet.of(Component.DRAFTPOOL), EnumSet.of(CellState.FULL)));
            return true;
        }

        return false;
    }

    /**
     * Activate one of the 3 toolcards.
     * @param toolCardId index of toolcard selected
     * @return true if successfully activated
     */
    boolean activateToolcard(String username, int toolCardId) {
        PlayerState newState;
        Player currentPlayer = match.getPlayerQueue().peek();
        PlayerMove playerMove = new PlayerMove<>(currentPlayer, null);
        ToolCard toolCardActivated = match.getToolCards()[toolCardId];

        boolean isPlayerEqual = match.getPlayerByName(username).equals(currentPlayer);
        boolean isPossibleActionActivateToolcard = (currentPlayer.getPossibleActions().contains(PossibleAction.ACTIVATE_TOOLCARD));
        boolean isPlayerEnoughTokens = (currentPlayer.getToken() >= toolCardActivated.getCost());

        //Checks if the player's name coincides with current player in queue, if 'ACTIVATE_TOOLCARD' is a possible action, if prechecks of toolcard are true and if the player has enough tokens
        if (isPlayerEqual && isPossibleActionActivateToolcard && Checks.get(toolCardActivated.getId()).apply(match, playerMove) && isPlayerEnoughTokens) {

            savedBoard = currentPlayer.getBoard().saveState();
            savedDraftpool = match.getDraftPool().saveState();
            savedRoundtracker = match.getRoundTracker().saveState();
            oldCost = toolCardActivated.getCost();

            //Creates a new toolcard controller with the toolcard activated
            this.toolcard = new ToolCardController(match, toolCardActivated);
            //Sets the toolcard activated inside the player
            currentPlayer.setActivatedToolcard(toolCardActivated);
            //The first operation of the toolcard is done
            newState = toolcard.handleMove(playerMove);
            //Subtract player's tokens based on the toolcard cost
            currentPlayer.setToken(currentPlayer.getToken() - toolCardActivated.getCost());
            //Sets the new state inside the player
            currentPlayer.setState(newState);
            //Increase the cost of the toolcard
            toolCardActivated.setCost(2);
            return true;
        }

        return false;
    }

    /**
     * Pass the current player's turn. In case it's a forced pass, it cancels the current operation.
     * @param username of the player
     * @return true if successfully passed the turn
     */
    boolean passTurn(String username) {
        Player currentPlayer = match.getPlayerQueue().peek();

        boolean isPlayerEqual = match.getPlayerByName(username).equals(currentPlayer);

        //Checks if the player's name coincides with the current player of the queue and if it contains 'PASS_TURN' possible action
        if(isPlayerEqual && currentPlayer.getPossibleActions().contains(PossibleAction.PASS_TURN)) {
            //Check if the current player's state is not 'YOUR_TURN'
            if(currentPlayer.getState().get() != EnumState.YOUR_TURN)
                cancelOperation(username);
            //Removes all possible actions (it will an empty enumset)
            currentPlayer.possibleActionsRemoveAll();
            //His state it's 'IDLE' and the player will be dequeued
            currentPlayer.setState(new PlayerState(EnumState.IDLE));
            return true;
        }

        return false;
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
            return !state.getActor().getPossibleActions().contains(PossibleAction.PICK_DIE);
        }

        /**
         * Check if the player is on his first or second turn
         * @param match of the game
         * @return true if it's on his 1st turn, false if it's on his 2nd turn
         */
        private static boolean checkTurn(Match match) {
            int queueSize = match.getPlayerQueue().size();
            Player[] tmp = match.getPlayerQueue().toArray(new Player[queueSize]);
            for(int i = 1; i < queueSize; i++) {
                if(tmp[i] == tmp[0])
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
            return move.getActor().getBoard().countDice() >= number;
        }

        /**
         * Check if there is any die on match's roundtracker
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

            BiFunction<Match, PlayerMove, Boolean> tc7 = (match, playerMove) -> checkTurn(match) && checkDiePicked(playerMove);

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

            BiFunction<Match, PlayerMove, Boolean> tc11 = (match, playerMove) -> checkDiceOnBoard(playerMove, 2) && checkDiceOnRoundtracker(match, 1);

            tmpChecks.put(11, tc11);

            checks = Collections.unmodifiableMap(tmpChecks);
        }

        static BiFunction<Match, PlayerMove, Boolean> get(int mapIndex){
            return checks.get(mapIndex);
        }
    }
}
