package it.polimi.se2018.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * This class describes the Extractor object
 * @author ontech7
 * @param <T>, generic object that needs to be extracted
 */
public class Extractor<T extends Serializable> implements Serializable{
    private ArrayList<T> container;

    public Extractor(){
        container = new ArrayList<>();
    }

    /**
     * Inserts a new element in the container
     * @param elem object to insert
     */
    public void insert(T elem) {
        container.add(elem);
    }

    /**
     * Extracts a random element from the container
     * @return an element and removes it from the container
     */
    public T extract() {
        return container.remove(new Random().nextInt(container.size()));
    }
}
