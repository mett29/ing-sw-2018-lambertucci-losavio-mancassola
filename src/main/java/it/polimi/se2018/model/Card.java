package it.polimi.se2018.model;

//The class that describes the card (objective card or tool card)
public interface Card {
    /**
     * Get the description of the card
     * @return description
     */
    public String getDescription();

    /**
     * Get the name of the card
     * @return name
     */
    public String getTitle();
}
