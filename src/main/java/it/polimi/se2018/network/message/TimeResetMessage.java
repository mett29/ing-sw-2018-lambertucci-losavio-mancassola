package it.polimi.se2018.network.message;

public class TimeResetMessage extends Message {
    /**
     * Constructor
     *
     */
    public TimeResetMessage() {
        super("admin", Content.TIME_RESET);
    }
}
