package it.polimi.se2018.model;

//The class that describes the Switch action
public class Switch implements Action {
    private DieCoord die1;
    private DieCoord die2;

    public Switch(DieCoord die1, DieCoord die2) {
        this.die1 = die1;
        this.die2 = die2;
    }

    public PlacementError check(){
        //TODO: need memento in Board to implement
        return null;
    }

    public void perform(){
        Die tmp = die1.get();
        die1.set(die2.get());
        die2.set(tmp);
    }
}
