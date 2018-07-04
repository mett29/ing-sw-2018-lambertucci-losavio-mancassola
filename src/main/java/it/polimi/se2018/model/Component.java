package it.polimi.se2018.model;

/**
 * Enumerator that contains some key components of the game
 * @author ontech7
 */
public enum Component {
    DRAFTPOOL, BOARD, ROUNDTRACKER;

    @Override
    public String toString() {
        switch(this.ordinal()){
            case 0:
                return "Draft Pool";
            case 1:
                return "Board";
            case 2:
                return "Round Tracker";
            default:
                return "Component";
        }
    }
}
