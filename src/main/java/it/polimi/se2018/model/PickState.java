package it.polimi.se2018.model;

import java.util.EnumSet;

public class PickState extends PlayerState {
    private EnumSet<Component> activeContainers;
    private EnumSet<CellState> cellStates;
    //private Color dieColor;

    public PickState(EnumSet<Component> activeContainers, EnumSet<CellState> cellStates){
        super(EnumState.PICK);
        this.activeContainers = activeContainers;
        this.cellStates = cellStates;
    }

    public EnumSet<Component> getActiveContainers() { return this.activeContainers; }

    public EnumSet<CellState> getCellStates() { return this.cellStates; }
}
