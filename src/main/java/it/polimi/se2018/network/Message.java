package it.polimi.se2018.network;

import java.io.Serializable;

/**
 * This class encapsulates a message that can be sent over the network
 */
public class Message implements Serializable {
    public final String username;
    public final String content;

    /**
     * Constructor
     * @param username Who created the message
     * @param content Content of the message
     */
    public Message(String username, String content){
        this.username = username;
        this.content = content;
    }

    enum Type{
        LOGIN, PLAYERMOVE, MATCHSTATE
    }
}
