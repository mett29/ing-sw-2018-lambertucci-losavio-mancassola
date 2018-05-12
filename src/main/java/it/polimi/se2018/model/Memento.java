package it.polimi.se2018.model;

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
