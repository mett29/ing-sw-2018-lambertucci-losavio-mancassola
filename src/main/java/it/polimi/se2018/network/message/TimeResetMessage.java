package it.polimi.se2018.network.message;

/**
 * Message sent by the server to every client whenever the in-game timer ends.
 */
public class TimeResetMessage extends Message {
    public TimeResetMessage() {
        super("admin", Content.TIME_RESET);
    }
}
