package it.polimi.se2018.network.server.rmi;

import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.ClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is the interface implemented by {@link ServerImplementation}
 * It needs to be public and extends {@link Remote} interface
 * @author mett29
 */
public interface ServerInterface extends Remote {

    void register(String message, ClientInterface client) throws RemoteException;

    /**
     * Send message to server
     * @param message Message to be sent
     * @throws RemoteException Connection has problems
     */
    void send(Message message) throws RemoteException;
}
