package it.polimi.se2018.model;

/**
 * Enumerator that contains all the possible colors
 * @author ontech7
 */
public enum Color {
    RED, BLUE, GREEN, PURPLE, YELLOW;

    @Override
    public String toString() {
        switch(this.ordinal()){
            case 0:
                return "r";
            case 1:
                return "b";
            case 2:
                return "g";
            case 3:
                return "p";
            case 4:
                return "y";
            default:
                return "";
        }
    }
}
