package it.polimi.se2018.model;

import java.util.HashMap;
import java.util.Map;

//The class that describes a dice container
public class DiceContainer {
    private Map<Integer, Die> container;
    private int maxSize;

    public DiceContainer(int size){
        container = new HashMap<>();
        maxSize = size;
    }
    /**
     * Getter of the container list
     * @return the container
     */
    public Map<Integer, Die> getDice() {
        return container;
    }

    /**
     * Getter of the size of the container
     * @return the size
     */
    public int getCurrentSize() {
        return container.size();
    }

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

    boolean isEmpty(int index){
        return !container.containsKey(index);
    }
}
