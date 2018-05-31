package it.polimi.se2018.network.client;

import it.polimi.se2018.network.message.Message;

public class QueueRequest extends Message {
    public final int playerNumber;
    public QueueRequest(String username, int playerNumber) {
        super(username, Content.QUEUE);
        this.playerNumber = playerNumber;
    }
}
