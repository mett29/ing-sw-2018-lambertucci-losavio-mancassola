package it.polimi.se2018.model;

//The class that describes the objective card (private and public)
public abstract class ObjCard extends Card {
    public ObjCard(){}
    //gets the description of the card
    public abstract String getDescription();
    //gets the title of the card
    public abstract String getTitle();
    //returns bonus points for a given board (can be 0)
    public abstract int getBonus(Board board);
}
