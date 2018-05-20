package it.polimi.se2018.model;

/**
 * This class contains the DiceContainer and its index
 * @author ontech7
 */
public class DiceContainerCoord implements DieCoord {
    private DiceContainer container;
    private int index;

    private DiceContainer savedContainer;

    public DiceContainerCoord(DiceContainer container, int index) {
        if(container == null)
            throw new NullPointerException();

        this.container = container;

        if(0 > index || index > container.getMaxSize())
            throw new IndexOutOfBoundsException("index must be in [0, container.maxSize[");

        this.index = index;
        savedContainer = null;
    }

    /**
     * Getter of the Die pointed by this coordinate
     * @return `Die` pointed by DieCoord
     */
    public Die get() { return this.container.getDie(index); }

    /**
     * Setter of the Die pointed by this coordinate
     * @param die object to set
     */
    public void set(Die die) { this.container.setDie(index, die); }

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

    /**
     * Helpers of the Memento pattern
     */
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
