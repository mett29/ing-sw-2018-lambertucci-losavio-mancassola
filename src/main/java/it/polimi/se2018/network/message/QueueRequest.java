package it.polimi.se2018.network.message;

/**
 * Used in the Server class when the clients try to login and they are put in the queue
 * @author MicheleLambertucci
 */
public class QueueRequest extends Message {
    public QueueRequest(String username) {
        super(username, Content.QUEUE);
    }
}
