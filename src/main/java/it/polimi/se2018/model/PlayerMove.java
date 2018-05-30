package it.polimi.se2018.model;

/**
 * A general purpose class that represents a move created by a player
 * @author MicheleLambertucci
 * @param <T> Type of content of the move (DieCoord[] for cell selections, Integer for value selection, etc...)
 */
public class PlayerMove<T> {
    private Player actor;
    private T move;
    private PossibleAction typeAction;

    public PlayerMove(Player actor, T move, PossibleAction typeAction){
        if(actor == null)
            throw new NullPointerException("The actor doesn't exist");
        this.actor = actor;
        this.move = move;
        this.typeAction = typeAction;
    }

    /**
     * Getter of move
     * @return move contained in PlayerMove
     */
    public T getMove(){
        return move;
    }

    public PossibleAction getTypeAction() {
        return typeAction;
    }

    /**
     * Set the state of the player who created the action
     * @param state State to be setted
     */
    public void setActorState(PlayerState state){
        if(state == null)
            throw new NullPointerException("`state` must be not null");
        actor.setState(state);
    }

    /**
     * @return the actor of the move
     */
    public Player getActor() { return actor; }
}
