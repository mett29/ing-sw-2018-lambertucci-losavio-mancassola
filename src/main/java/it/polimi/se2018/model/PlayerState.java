package it.polimi.se2018.model;

/**
 * This class represents the Player's state
 * The state is contained in {@link EnumState}
 * @author ontech7
 */
public class PlayerState {
    private EnumState state;

    public PlayerState(EnumState newState) {
        this.state = newState;
    }

    public EnumState getState() { return this.state; }
}
