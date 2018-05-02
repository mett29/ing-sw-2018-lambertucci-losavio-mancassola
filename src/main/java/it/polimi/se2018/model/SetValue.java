package it.polimi.se2018.model;

//The class that describes the SetValue action
public class SetValue implements Action {
    private DieCoord die;
    private int value;

    public SetValue(DieCoord die, int value) {
        this.die = die;
        this.value = value;
    }

    @Override
    public PlacementError check() {
        return new PlacementError();
    }

    @Override
    public void perform() {
        Die newDie = new Die(value, die.get().getColor());
        die.set(newDie);
    }
}
