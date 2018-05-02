package it.polimi.se2018.model;

public class MoveToDicebag implements Action {
    private DieCoord dieCoord;

    public MoveToDicebag(DieCoord dieCoord) {
        this.dieCoord = dieCoord;
    }

    @Override
    public PlacementError check() {
        return new PlacementError();
    }

    @Override
    public void perform() {
        //TODO: need Match to implement
    }
}
