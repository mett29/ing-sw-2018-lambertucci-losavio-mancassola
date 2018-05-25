package it.polimi.se2018.network.server.rmi;

import it.polimi.se2018.network.Message;
import it.polimi.se2018.network.client.ClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {

    void register(String message, ClientInterface client) throws RemoteException;

    /**
     * Send message to server
     * @param message Message to be sent
     * @throws RemoteException Connection has problems
     */
    void send(Message message) throws RemoteException;
}
