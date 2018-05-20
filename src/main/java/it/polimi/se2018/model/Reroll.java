package it.polimi.se2018.model;

/**
 * This class describes the action 'Reroll'
 * @author MicheleLambertucci
 */
public class Reroll implements Action {
    private DieCoord dieCoord;

    public Reroll(DieCoord dieCoord) {
        this.dieCoord = dieCoord;
    }

    @Override
    public PlacementError check() {
        return new PlacementError();
    }

    @Override
    public void perform() {
        dieCoord.get().randomize();
    }
}
