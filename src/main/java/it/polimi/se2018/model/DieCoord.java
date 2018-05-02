package it.polimi.se2018.model;

public interface DieCoord {
    /**
     * Getter of the Die pointed by this coordinate
     * @return `Die` pointed by DieCoord
     */
    public Die get();

    /**
     * Setter of the Die pointed by this coordinate
     * @param die object to set
     */
    public void set(Die die);

    /**
     * Check possible errors if `die` was to be placed in coordinate
     * @param die object to be tested
     * @return placement errors container
     */
    public PlacementError isAllowed(Die die);
}
