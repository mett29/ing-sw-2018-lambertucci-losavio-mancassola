package it.polimi.se2018.model;

import java.io.Serializable;

/**
 * This class represents the Player's state
 * The state is contained in {@link EnumState}
 * @author ontech7
 */
public class PlayerState implements Serializable {
    private EnumState state;

    public PlayerState(EnumState newState) {
        this.state = newState;
    }

    public EnumState get() { return this.state; }
}
