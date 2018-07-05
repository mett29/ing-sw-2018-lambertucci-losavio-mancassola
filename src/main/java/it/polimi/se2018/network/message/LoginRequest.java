package it.polimi.se2018.network.message;

public class LoginRequest extends Message {
    public final Type type;

    /**
     * Constructor
     * @param username Who created the message
     */
    public LoginRequest(String username) {
        super(username, Content.LOGIN);
        type = Type.REQUEST;
    }
}
