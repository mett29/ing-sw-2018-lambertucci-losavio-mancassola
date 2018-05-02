package it.polimi.se2018.model;

import java.util.ArrayList;
import java.util.Random;

//The class that describes the extractor with generic
public class Extractor<T> {
    protected ArrayList<T> container;

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
     * Extracts a random element from the container with a specified size
     * @param bound integer as index
     * @return an element and removes it from the container
     */
    public T extract(int bound) {
        return container.remove(new Random().nextInt(bound));
    }
}
