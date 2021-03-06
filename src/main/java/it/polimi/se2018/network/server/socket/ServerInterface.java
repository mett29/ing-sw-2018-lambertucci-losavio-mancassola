package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.message.Message;

/**
 * Interface used by SocketConnection to communicate with the server
 * @author mett29
 */
public interface ServerInterface {
    /**
     * Send message to server
     * @param message Message to be sent
     */
    void send(Message message);
}
