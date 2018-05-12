package it.polimi.se2018.model;

public class MoveToDicebag implements Action {
    private DieCoord dieCoord;
    private Match match;

    public MoveToDicebag(DieCoord dieCoord, Match match) {
        this.dieCoord = dieCoord;
        this.match = match;
    }

    @Override
    public PlacementError check() {
        return new PlacementError();
    }

    @Override
    public void perform() {
        match.insertDie(dieCoord.get());
        dieCoord.set(null);
    }
}
