package it.polimi.se2018.model;

import java.io.Serializable;

/**
 * This class describes the object Card (both objective card and tool card)
 * @author MicheleLambertucci
 * Extended by {@link ObjCard}
 */
public interface Card extends Serializable {
    /**
     * @return the card's description
     */
    String getDescription();

    /**
     * @return the card's name
     */
    String getTitle();
}
