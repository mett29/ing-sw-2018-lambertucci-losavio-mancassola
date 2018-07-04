package it.polimi.se2018.model;

/**
 * This interface describes an action taken by a player
 * @author MicheleLambertucci
 */
public interface Action {
    /**
     * Check possible errors before the action is performed
     * @return the related error
     */
    PlacementError check();

    /**
     * Perform the action
     */
    void perform();
}
