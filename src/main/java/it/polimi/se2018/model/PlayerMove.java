package it.polimi.se2018.model;

/**
 * A general purpose class that represents a move created by a player
 * @param <T> Type of content of the move (DieCoord[] for cell selections, Integer for value selection, etc...)
 */
public class PlayerMove<T> {
    private Player actor;
    private T move;

    public PlayerMove(Player actor, T move){
        this.actor = actor;
        this.move = move;
    }

    /**
     * Getter of move
     * @return move contained in PlayerMove
     */
    public T getMove(){
        return move;
    }

    /**
     * Set the state of the player who created the action
     * @param state State to be setted
     */
    public void setActorState(PlayerState state){
        actor.setState(state);
    }
}
