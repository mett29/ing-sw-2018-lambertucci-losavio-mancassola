package it.polimi.se2018.model;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.EnumSet;
import java.util.Set;

/**
 * This class describes the object Player
 * @version 1.1
 * @author mett29
 */
public class Player implements Serializable {
    private Board board;
    private int token;
    private String name;
    private PrivateObjCard privateObjCard;
    private ToolCard activatedToolcard;
    private PlayerState state;
    private EnumSet<PossibleAction> possibleActions;
    private Die pickedDie;
    private boolean winner;
    private boolean disconnected;

    // Player constructor
    public Player(String name) {
        if(name == null)
            throw new NullPointerException("`name` must be not null");
        this.name = name;
        this.state = new PlayerState(EnumState.IDLE);
        pickedDie = null;
        this.winner = false;
        this.disconnected = false;
    }

    /**
     * @return the player's name
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return the player's board
     */
    public Board getBoard() {
        return this.board;
    }

    public void setBoard(Board board) {
        if(board == null)
            throw new NullPointerException("`board` must be not null");
        this.board = board;
        this.token = board.getBoardDifficulty();
    }

    /**
     * @return the number of tokens of the player
     */
    public int getToken() {
        return this.token;
    }

    /**
     * Setter of the tokens of the player
     * @param token number of tokens
     */
    public void setToken(int token) {
        if(token < 0){
            throw new InvalidParameterException("`token` must be grater than 0");
        }
        this.token = token;
    }

    /**
     * @return the private objective card assigned to the player
     */
    public PrivateObjCard getPrivateObjCard() {
        return privateObjCard;
    }

    /**
     * Setter of the player's private objective card
     * @param privateObjCard to assign to the player
     */
    public void setPrivateObjCard(PrivateObjCard privateObjCard) {
        if(privateObjCard == null)
            throw new NullPointerException("`privateObjCard` must be not null");
        this.privateObjCard = privateObjCard;
    }

    /**
     * @return the toolcard activated by the player
     */
    public ToolCard getActivatedToolcard() {
        return this.activatedToolcard;
    }

    /**
     * Setter of the activated toolcard
     * @param activatedToolcard the toolcard activated by the player
     */
    public void setActivatedToolcard(ToolCard activatedToolcard) {
        if(activatedToolcard == null)
            throw new NullPointerException("`activatedToolcard` must be not null");
        this.activatedToolcard = activatedToolcard;
    }

    /**
     * Deactivate a toolcard
     */
    public void deactivateToolcard() { activatedToolcard = null; }

    /**
     * Setter of the player's state
     * @param state object to set
     */
    public void setState(PlayerState state){
        if(state == null)
            throw new NullPointerException("`state` must be not null");
        this.state = state;
    }

    /**
     * @return the player's state
     */
    public PlayerState getState() { return state; }

    /**
     * Sets up all possible actions of the player at the start of the round
     */
    public void possibleActionsSetUp() {
        possibleActions = EnumSet.of(PossibleAction.ACTIVATE_TOOLCARD, PossibleAction.PICK_DIE, PossibleAction.PASS_TURN);
    }

    /**
     * Removes all possible actions of the player
     */
    public void possibleActionsRemoveAll() {
        possibleActions = EnumSet.noneOf(PossibleAction.class);
    }

    /**
     * Removes a single possible action
     * @param action to be removed
     * @return true if the action is in the EnumSet, false otherwise
     */
    public boolean possibleActionsRemove(PossibleAction action) {
        return possibleActions.remove(action);
    }

    /**
     * @return all possible actions of the player
     */
    public Set<PossibleAction> getPossibleActions() {
        return possibleActions;
    }

    /**
     * Set a player if it's the winner of the match
     * @param winner of the match
     */
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /**
     * Check if the player is the winner
     * @return true if he is the winner, false otherwise
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * Set a player if it's disconnected
     * @param disconnected object to set
     */
    public void setDisconnected(boolean disconnected) {
        this.disconnected = disconnected;
    }

    /**
     * Check if the player is disconnected
     * @return true if disconnect, false otherwise
     */
    public boolean isDisconnected() {
        return disconnected;
    }

    /**
     * Show if a player has a die in his hand
     * @return the picked die
     */
    public Die getPickedDie() {
        return pickedDie;
    }

    /**
     * Set the die picked from the player
     * @param pickedDie die picked
     */
    public void setPickedDie(Die pickedDie) {
        this.pickedDie = pickedDie;
    }
}
