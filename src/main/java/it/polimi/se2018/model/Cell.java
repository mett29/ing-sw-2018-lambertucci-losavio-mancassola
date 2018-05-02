package it.polimi.se2018.model;

//The class that describes a cell of the board
public class Cell {
    private Restriction restriction;
    private Die die = null;

    public Cell() {}

    //gets any error
    public PlacementError isDieAllowed(Die die) {

    }

    //gets the die that is inside the cell (if there is one)
    public Die getDie() {

    }

    //sets a die inside the cell
    public void setDie(Die die) {

    }

    public boolean isEmpty(){
        return die == null;
    }
}
