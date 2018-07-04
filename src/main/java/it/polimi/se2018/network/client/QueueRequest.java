package it.polimi.se2018.network.client;

import it.polimi.se2018.network.message.Message;

public class QueueRequest extends Message {
    QueueRequest(String username) {
        super(username, Content.QUEUE);
    }
}
