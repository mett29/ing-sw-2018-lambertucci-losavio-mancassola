package it.polimi.se2018.model;

import java.util.Iterator;

//The class that describes a board
public class Board implements Iterable<Cell>{
    private Cell[][] window;

    public Board() {}

    //gets any error
    public PlacementError isDieAllowed(int x, int y, Die die) {

    }

    //gets a die from a specified coordinate of the board
    public Die getDie(int x, int y) {

    }

    //sets a die in a specified coordinate of the board
    public void setDie(int x, int y, Die die) {

    }

    //gets all the 8 neighbours dice of a specified coordinate of the board
    /*@ helper @*/ private Die[] getNeighbours(int x, int y) {

    }

    @Override
    public Iterator<Cell> iterator() {
        return new Iterator<Cell>() {
            private int index = 0;

            private int getX() {
                return index % 5;
            }

            private int getY() {
                return index / 5; // integer division
            }

            @Override
            public boolean hasNext() {
                return getX() < 5 && getY() < 4;
            }

            @Override
            public Cell next() {
                index++;
                return window[getX()][getY()];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
