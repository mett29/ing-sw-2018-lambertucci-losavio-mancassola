package it.polimi.se2018.model;

/**
 * A general purpose class that represents a move created by a player
 * @author MicheleLambertucci
 * @param <T> Type of content of the move (DieCoord[] for cell selections, Integer for value selection, etc...)
 */
public class PlayerMove<T> {
    private Player actor;
    private T move;

    public PlayerMove(Player actor, T move){
        if(actor == null)
            throw new NullPointerException("The actor doesn't exist");
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
     * @return the actor of the move
     */
    public Player getActor() { return actor; }
}
