package it.polimi.se2018.model;

//The class that describes the Flip action
public class Flip implements Action {
    private DieCoord dieCoord;

    public Flip(DieCoord dieCoord) {
        this.dieCoord = dieCoord;
    }

    @Override
    public PlacementError check() {
        return new PlacementError();
    }

    @Override
    public void perform() {
        int value = dieCoord.get().getValue();
        int newvalue = 7 - value;
        Die newdie = new Die(newvalue, dieCoord.get().getColor());
        dieCoord.set(newdie);
    }
}
