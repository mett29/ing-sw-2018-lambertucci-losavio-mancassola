package it.polimi.se2018.network.message;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.message.Message;

public class MatchStartMessage extends Message {
    public final Match payload;
    public final int timerValue;

    /**
     * Constructor
     *
     * @param payload  Initial state of the match
     */
    public MatchStartMessage(Match payload, int timerValue) {
        super("admin", Content.MATCH_START);
        this.payload = payload;
        this.timerValue = timerValue;
    }
}
