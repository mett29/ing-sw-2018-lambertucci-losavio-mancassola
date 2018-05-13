package it.polimi.se2018.model;

public class PlayerState {
    private EnumState state;

    public PlayerState(EnumState newState) {
        this.state = newState;
    }

    public EnumState getState() { return this.state; }
}
