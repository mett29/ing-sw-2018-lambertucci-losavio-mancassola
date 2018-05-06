package it.polimi.se2018.model;

//The class that contains the dice container and its index
public class DiceContainerCoord implements DieCoord {
    private DiceContainer container;
    private int index;

    private DiceContainer savedContainer;

    public DiceContainerCoord(DiceContainer container, int index) {
        this.container = container;
        this.index = index;
        savedContainer = null;
    }

    /**
     * Getter of the Die pointed by this coordinate
     * @return `Die` pointed by DieCoord
     */
    public Die get() {
        return this.container.getDie(index);
    }

    /**
     * Setter of the Die pointed by this coordinate
     * @param die object to set
     */
    public void set(Die die) {
        this.container.setDie(index, die);
    }

    /**
     * Check possible errors if `die` was to be placed in coordinate
     * @param die object to be tested
     * @return placement errors container
     */
    public PlacementError isAllowed(Die die) {
        if(!container.isEmpty(index))
            return new PlacementError(Flags.NOTEMPTY);
        return new PlacementError();
    }

    @Override
    public void saveState() {
        savedContainer = container.saveState();
    }

    @Override
    public void restoreState() {
        if(savedContainer == null){ throw new NullPointerException(); }
        container.restoreState(savedContainer);
        savedContainer = null;
    }
}
