package it.polimi.se2018.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class describes a generic object which contains dice
 * @author ontech7
 */
public class DiceContainer implements Iterable<Die>, Memento<DiceContainer> {
    private Map<Integer, Die> container;
    private int maxSize;

    public DiceContainer(int size){
        container = new HashMap<>();
        maxSize = size;
    }

    /**
     * Copy constructor
     * @param other object to deep copy
     */
    public DiceContainer(DiceContainer other){
        if(other == null)
            throw new NullPointerException("'other' must be not null");
        container = new HashMap<>();
        maxSize = other.maxSize;
        for(Die i : other){
            if(i != null)
                insert(new Die(i));
        }
    }

    /**
     * @return the container
     */
    public Map<Integer, Die> getDice() {
        return container;
    }

    /**
     * @return the container's size
     */
    public int getCurrentSize() {
        return container.size();
    }

    /**
     * @return the max value of the container's size
     */
    public int getMaxSize(){
        return maxSize;
    }

    Die getDie(int index){
        return container.getOrDefault(index, null);
    }

    void setDie(int index, Die die){
        if(index > maxSize)
            throw new IndexOutOfBoundsException();
        container.put(index, die);
    }

    void insert(Die die) {
        int size = getCurrentSize();
        setDie(size, die);
    }

    /**
     * Check if the container is empty or not
     * @param index, the hypotethical position in the container
     * @return true or false
     */
    boolean isEmpty(int index){
        return !container.containsKey(index);
    }

    @Override
    public Iterator<Die> iterator() {
        return container.values().iterator();
    }

    /**
     * Helpers of the Memento pattern
     */
    @Override
    public DiceContainer saveState() {
        return new DiceContainer(this);
    }

    @Override
    public void restoreState(DiceContainer savedState) {
        this.container = savedState.container;
        this.maxSize = savedState.maxSize;
    }
}
