package it.polimi.se2018.model;

import java.util.EnumSet;

//The class that describes if there is any error in the placement
public class PlacementError {
    private EnumSet<Flags> errorByte;

    public PlacementError() { errorByte = EnumSet.noneOf(Flags.class); }

    public PlacementError(Flags flag) { errorByte = EnumSet.of(flag); }

    private PlacementError(EnumSet<Flags> flags) { errorByte = flags; }

    //returns true if the die placed near another die has the same value

    /**
     * Check if the die placed near another die has the same value
     * @return true if same value
     */
    public boolean isValueError() {
        return errorByte.contains(Flags.VALUE);
    }

    //returns true if the die placed near another die has the same color
    /**
     * Check if the die placed near another die has the same value
     * @return true if same color
     */
    public boolean isColorError() {
        return errorByte.contains(Flags.COLOR);
    }

    //returns true if the player is trying to place a die in a not-empty cell
    /**
     * Check if the player is trying to place a die in a not-empty cell
     * @return true if not empty
     */
    public boolean isNotEmptyError() {
        return errorByte.contains(Flags.NOTEMPTY);
    }

    //returns true if the die is not near another die
    /**
     * Check if the die is not near another die
     * @return true if not near die
     */
    public boolean isNotNearOthersError() {
        return errorByte.contains(Flags.NEIGHBOURS);
    }

    //returns true if the first die is not on an edge
    /**
     * Check if the first die is not on an edge
     * @return true if not on edge
     */
    public boolean isNotOnEdgeError() {
        return errorByte.contains(Flags.EDGE);
    }

    /**
     * Unite two PlacementError(s) in one
     * @param a object to unite
     * @param b object to unite
     * @return the two object united
     */
    public static PlacementError union(PlacementError a, PlacementError b) {
        EnumSet<Flags> c = EnumSet.copyOf(a.errorByte);
        c.addAll(b.errorByte);

        return new PlacementError(c);
    }

    /**
     * Check if it has any error inside the enumset
     * @return true if any error
     */
    public boolean hasError() {
        return !errorByte.isEmpty();
    }

    public boolean isEqual(EnumSet<Flags> flags){
        return errorByte.containsAll(flags);
    }
}
