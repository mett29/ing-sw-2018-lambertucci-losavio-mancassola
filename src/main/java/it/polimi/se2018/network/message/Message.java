package it.polimi.se2018.network.message;

import java.io.Serializable;

/**
 * This class encapsulates a message that can be sent over the network
 * @author MicheleLambertucci
 */
public class Message implements Serializable {
    public final String username;
    public final Content content;

    /**
     * Constructor
     * @param username Who created the message
     * @param content Content of the message
     */
    public Message(String username, Content content){
        this.username = username;
        this.content = content;
    }

    public enum Content {
        LOGIN, PLAYER_MOVE, MATCH_STATE, TOOLCARD_REQUEST, TOOLCARD_RESPONSE, PATTERN_REQUEST, QUEUE, MATCH_START, NORMAL_MOVE, PASS, UNDO_REQUEST, UNDO_RESPONSE, PATTERN_RESPONSE, TIME_RESET
    }

    public enum Type {
        REQUEST, OK, FAILURE
    }
}
