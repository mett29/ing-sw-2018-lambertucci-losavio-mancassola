package it.polimi.se2018.model;

import java.util.EnumSet;
import java.util.Set;

/**
 * This class represents the generic placement error
 * @author MicheleLambertucci, ontech7
 */
public class PlacementError {
    private EnumSet<Flag> errorByte;

    public PlacementError() { errorByte = EnumSet.noneOf(Flag.class); }

    public PlacementError(Flag flag) { errorByte = EnumSet.of(flag); }

    private PlacementError(EnumSet<Flag> flags) { errorByte = flags; }

    /**
     * Check if the die placed near another die has the same value
     * @return true if same value
     */
    public boolean isValueError() {
        return errorByte.contains(Flag.VALUE);
    }

    /**
     * Check if the die placed near another die has the same color
     * @return true if same color
     */
    public boolean isColorError() {
        return errorByte.contains(Flag.COLOR);
    }

    /**
     * Check if the player is trying to place a die in a not-empty cell
     * @return true if not empty
     */
    public boolean isNotEmptyError() {
        return errorByte.contains(Flag.NOTEMPTY);
    }

    /**
     * Check if the die is not near another die
     * @return true if not near die
     */
    public boolean isNotNearOthersError() {
        return errorByte.contains(Flag.NEIGHBOURS);
    }

    /**
     * Check if the first die is not on an edge
     * @return true if not on edge
     */
    public boolean isNotOnEdgeError() {
        return errorByte.contains(Flag.EDGE);
    }

    /**
     * Unite two PlacementError(s) in one
     * @param a object to unite
     * @param b object to unite
     * @return the two object united
     */
    static PlacementError union(PlacementError a, PlacementError b) {
        EnumSet<Flag> c = EnumSet.copyOf(a.errorByte);
        c.addAll(b.errorByte);

        return new PlacementError(c);
    }

    /**
     * Check if it has any error inside the enumset
     * @return true if any error
     */
    boolean hasError() {
        return !errorByte.isEmpty();
    }

    public boolean hasErrorFilter(Set<Flag> filters){
        EnumSet<Flag> tmpErrs = errorByte.clone();
        tmpErrs.removeAll(filters);
        return !tmpErrs.isEmpty();
    }

    public boolean hasNoErrorExceptEdgeFilter(Set<Flag> filters) {
        Set<Flag> tmpErrs = errorByte.clone();
        tmpErrs.removeAll(filters);
        return tmpErrs.equals(EnumSet.of(Flag.EDGE)) || tmpErrs.isEmpty();
    }

    public boolean hasNoErrorExceptEdge(){ return isEqual(EnumSet.of(Flag.EDGE)) || !hasError(); }

    boolean isEqual(EnumSet<Flag> flags){
        return errorByte.equals(flags);
    }

    boolean isEqual(PlacementError other){
        return errorByte.containsAll(other.errorByte);
    }
}
