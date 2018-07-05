package it.polimi.se2018.network.client;

import it.polimi.se2018.network.message.Message;

/**
 * Used in the Server class when the clients try to login and they are put in the queue
 * @author MicheleLambertucci
 */
public class QueueRequest extends Message {
    QueueRequest(String username) {
        super(username, Content.QUEUE);
    }
}
