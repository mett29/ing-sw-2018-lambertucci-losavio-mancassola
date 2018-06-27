package it.polimi.se2018.network.message;

public class TimeResetMessage extends Message {
    /**
     * Constructor
     *
     * @param username Who created the message
     * @param content  Content of the message
     */
    public TimeResetMessage(String username, Content content) {
        super(username, content);
    }
}
