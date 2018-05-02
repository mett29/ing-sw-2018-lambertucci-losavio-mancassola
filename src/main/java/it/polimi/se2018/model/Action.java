package it.polimi.se2018.model;

//The interface that describes an action of a player
public interface Action {
    /**
     * Check possible errors if action was to be performed
     * @return
     */
    public PlacementError check();

    /**
     * Perform the action
     */
    public void perform();
}
