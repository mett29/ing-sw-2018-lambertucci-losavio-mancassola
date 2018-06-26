package it.polimi.se2018.network.message;

import it.polimi.se2018.model.Match;

public class LoginResponse extends Message{
    public final Type response;
    public final Match payload;

    /**
     * Constructor
     *
     * @param ok   False if login went wrong, true otherwise
     */
    public LoginResponse(boolean ok, Match payload) {
        super("admin", Content.LOGIN);
        if(ok){
            response = Type.OK;
            this.payload = payload;
        } else {
            response = Type.FAILURE;
            this.payload = null;
        }
    }
}
