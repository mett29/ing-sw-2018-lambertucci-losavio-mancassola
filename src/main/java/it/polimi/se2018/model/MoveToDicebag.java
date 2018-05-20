package it.polimi.se2018.model;

/**
 * This class represents the action 'MoveToDicebag'
 * @author MicheleLambertucci
 */
public class MoveToDicebag implements Action {
    private DieCoord dieCoord;
    private Match match;

    public MoveToDicebag(DieCoord dieCoord, Match match) {
        this.dieCoord = dieCoord;
        this.match = match;
    }

    // Check errors
    @Override
    public PlacementError check() {
        return new PlacementError();
    }

    // Perform the action
    @Override
    public void perform() {
        match.insertDie(dieCoord.get());
        dieCoord.set(null);
    }
}
