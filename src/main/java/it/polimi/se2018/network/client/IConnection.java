package it.polimi.se2018.network.client;

import it.polimi.se2018.network.Message;

import java.rmi.RemoteException;

public interface IConnection {
    /**
     * Send message to server
     * @param message Message to be sent
     * @throws RemoteException Connection not working
     */
    void send(Message message) throws RemoteException;
}
