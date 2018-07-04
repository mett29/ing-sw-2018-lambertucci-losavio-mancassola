package it.polimi.se2018.model;

import java.util.Set;

/**
 * This class describes the action 'PickState'
 * @author MicheleLambertucci
 */
public class PickState extends PlayerState {
    private Set<Component> activeContainers;
    private Set<CellState> cellStates;

    public PickState(Set<Component> activeContainers, Set<CellState> cellStates){
        super(EnumState.PICK);
        this.activeContainers = activeContainers;
        this.cellStates = cellStates;
    }

    public Set<Component> getActiveContainers() { return this.activeContainers; }

    public Set<CellState> getCellStates() { return this.cellStates; }
}
