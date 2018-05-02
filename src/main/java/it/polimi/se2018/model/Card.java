package it.polimi.se2018.model;

//The class that describes the card (objective card or tool card)
public abstract class Card {
    public Card() {}

    //gets the description of the card
    public abstract String getDescription();

    //gets the title of the card
    public abstract String getTitle();
}
