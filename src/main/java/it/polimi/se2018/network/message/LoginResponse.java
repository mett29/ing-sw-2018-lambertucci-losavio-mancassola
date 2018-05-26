package it.polimi.se2018.network.message;

public class LoginResponse extends Message{
    public final Type response;

    /**
     * Constructor
     *
     * @param ok   False if login went wrong, true otherwise
     */
    public LoginResponse(boolean ok) {
        super("admin", Content.LOGIN);
        if(ok){
            response = Type.OK;
        } else {
            response = Type.FAILURE;
        }
    }
}
