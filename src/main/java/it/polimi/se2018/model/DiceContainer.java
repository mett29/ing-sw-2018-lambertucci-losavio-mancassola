package it.polimi.se2018.model;

import java.util.ArrayList;

//The class that describes a dice container
public class DiceContainer extends Extractor<Die> {
    /**
     * Getter of the container list
     * @return the container
     */
    public ArrayList<Die> getDice() {
        return container;
    }

    /**
     * Getter of the size of the container
     * @return the size
     */
    public int getSize() {
        return container.size();
    }


}
