package it.polimi.se2018.model;

//The class that contains the dice container and its index
public class DiceContainerCoord implements DieCoord {
    private DiceContainer container;
    private int index;

    public DiceContainerCoord(DiceContainer container, int index) {
        this.container = container;
        this.index = index;
    }

    /**
     * Getter of the Die pointed by this coordinate
     * @return `Die` pointed by DieCoord
     */
    public Die get() {
        if(index <= container.getSize())
            return this.container.getDice().get(index);
        else
            return new Die(0, null); //maybe an error message it's better
    }

    /**
     * Setter of the Die pointed by this coordinate
     * @param die object to set
     */
    public void set(Die die) {
        this.container.insert(die);
    }

    /**
     * Check possible errors if `die` was to be placed in coordinate
     * @param die object to be tested
     * @return placement errors container
     */
    public PlacementError isAllowed(Die die) {
        // TODO
    }
}
