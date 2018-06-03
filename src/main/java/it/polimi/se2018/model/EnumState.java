package it.polimi.se2018.model;

/**
 * Enumerator that contains all the possible states in which a user could be
 * @author ontech7
 */
public enum EnumState {
    IDLE, YOUR_TURN, PICK, VALUE, UPDOWN, YESNO, REPEAT;

    @Override
    public String toString() {
        switch(this){
            case IDLE:
                return "Idle";
            case YOUR_TURN:
                return "Your turn";
            case PICK:
                return "Pick cell";
            case VALUE:
                return "Pick value";
            case UPDOWN:
                return "Pick +/-";
            case YESNO:
                return "Yes/No";
            case REPEAT:
                return "Repeat";
        }
        return "";
    }
}
