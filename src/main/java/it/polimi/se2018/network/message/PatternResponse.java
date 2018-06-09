package it.polimi.se2018.network.message;

/**
 * This class encapsulates a board's pattern response
 */
public class PatternResponse extends Message {
    public final int index;

    public PatternResponse(String username, int index) {
        super(username, Content.PATTERN_RESPONSE);
        this.index = index;
    }
}
