package it.polimi.se2018.network.message;

import it.polimi.se2018.network.client.ClientMove;

public class MoveMessage extends Message {
    public final ClientMove payload;
    /**
     * Constructor
     *
     * @param username Who created the message
     * @param payload
     */
    public MoveMessage(String username, ClientMove payload) {
        super(username, Content.PLAYER_MOVE);
        this.payload = payload;
    }
}
