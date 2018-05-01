package it.polimi.se2018.model;

//The class that describes if there is any error in the placement
public class PlacementError {
    private Byte flags;

    public PlacementError() {}

    //returns true if the die placed near another die has the same value
    public boolean isValueError() {

    }

    //returns true if the die placed near another die has the same color
    public boolean isColorError() {

    }

    //returns true if the player is trying to place a die in a not-empty cell
    public boolean isNotEmptyError() {

    }

    //returns true if the permission is not granted
    public boolean isPermissionError() {

    }

    //returns true if the die is not near another die
    public boolean isNotNearOthersError() {

    }

    //returns true if
    public boolean isNotOnEdgeError() {

    }

    public static PlacementError union(PlacementError a, PlacementError b) {

    }

    public boolean hasError() {

    }
}
