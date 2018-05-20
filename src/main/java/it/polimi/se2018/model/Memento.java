package it.polimi.se2018.model;

/**
 * Interface implemented by {@link DiceContainer}
 * @author mett29
 * @param <T> the object of which save a deep copy
 */
public interface Memento<T> {
    /**
     * Save a deep copy of the object
     * @return deep copy of the object
     */
    public T saveState();

    /**
     * Restore a previously saved state
     * @param savedState State to restore
     */
    public void restoreState(T savedState);
}
