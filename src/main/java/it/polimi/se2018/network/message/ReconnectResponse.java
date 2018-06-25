package it.polimi.se2018.network.message;

import it.polimi.se2018.model.Match;

public class ReconnectResponse extends Message{
    public final Type response;
    public final Match payload;

    /**
     * Constructor
     *
     * @param ok   False if login went wrong, true otherwise
     */
    public ReconnectResponse(boolean ok, Match payload) {
        super("admin", Content.RECONNECT);
        if(ok){
            response = Type.OK;
            this.payload = payload;
        } else {
            response = Type.FAILURE;
            this.payload = null;
        }
    }
}
