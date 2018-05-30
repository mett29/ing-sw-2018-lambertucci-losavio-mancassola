package it.polimi.se2018.network.message;

/**
 * This class encapsulates a board's pattern response
 */
public class PatternResponse extends Message {

    public final String patternName;

    public PatternResponse(String username, String patternName) {
        super(username, Content.PATTERN_RESPONSE);
        this.patternName = patternName;
    }
}
