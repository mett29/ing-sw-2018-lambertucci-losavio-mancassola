package it.polimi.se2018.network.message;

import it.polimi.se2018.model.Match;

public class MatchStateMessage extends Message{
    public final Match payload;

    /**
     * Constructor
     * @param match Match state contained in message
     */
    public MatchStateMessage(Match match) {
        super("admin", Content.MATCH_STATE);
        payload = match;
    }
}
