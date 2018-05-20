package it.polimi.se2018.model;

/**
 * This class describes the object Card (both objective card and tool card)
 * @author MicheleLambertucci
 * Extended by {@link ObjCard}
 */
public interface Card {
    /**
     * @return the card's description
     */
    public String getDescription();

    /**
     * @return the card's name
     */
    public String getTitle();
}
