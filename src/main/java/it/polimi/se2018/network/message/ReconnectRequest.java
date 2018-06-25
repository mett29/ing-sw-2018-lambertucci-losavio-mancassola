package it.polimi.se2018.network.message;

public class ReconnectRequest extends Message{
    public final Type type;

    /**
     * Constructor
     * @param username Who created the message
     */
    public ReconnectRequest(String username) {
        super(username, Content.RECONNECT);
        type = Type.REQUEST;
    }
}
