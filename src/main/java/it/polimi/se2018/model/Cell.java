package it.polimi.se2018.model;


/**
 * This class represents the object Cell of the board
 * @version 1.0
 */
public class Cell {
    private Restriction restriction;
    private Die die;

    /**
     * Create cell with a restriction
     * @param restriction Restriction to be set inside the cell. Set to `null` for a cell with no restriction.
     */
    public Cell(Restriction restriction) {
        this.die = null;
        this.restriction = restriction;
    }

    /**
     * Check placement errors if `die` was to be placed in this cell
     * @param die The Die to check
     * @return The related error
     */
    public PlacementError isDieAllowed(Die die) {

        PlacementError err = new PlacementError();

        // Check if the placed Die violate the restriction
        if(restriction != null) {
            err = PlacementError.union(err, restriction.isDieAllowed(die));
        }

        // Check if the Cell is occupied yet
        if (!isEmpty())
            err = PlacementError.union(err, new PlacementError(Flags.NOTEMPTY));

        return err;
    }

    /**
     * @return The Die in the Cell
     */
    public Die getDie() {
        return this.die;
    }

    /**
     * Set die in this cell
     * @param die The die to set
     */
    public void setDie(Die die) {
        this.die = die;
    }

    public Restriction getRestriction() { return this.restriction; }

    /**
     * This method check if a Cell is empty or not
     * @return True or False
     */
    public boolean isEmpty() {
        return die == null;
    }
}
