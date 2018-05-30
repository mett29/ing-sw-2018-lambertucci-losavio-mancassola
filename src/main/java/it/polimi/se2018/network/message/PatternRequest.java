package it.polimi.se2018.network.message;

import java.util.List;

/**
 * This class encapsulates a board's pattern request
 */
public class PatternRequest extends Message {

    private final List<String> patternNames;

    public PatternRequest(String username, List<String> patternNames) {
        super(username, Content.PATTERN_REQUEST);
        this.patternNames = patternNames;
    }
}
