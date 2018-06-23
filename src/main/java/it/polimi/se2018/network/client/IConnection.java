package it.polimi.se2018.network.client;

import it.polimi.se2018.network.message.Message;

import java.rmi.RemoteException;

public interface IConnection {

    void registerClient(String username);

    /**
     * Send message to server
     * @param message Message to be sent
     * @throws RemoteException Connection not working
     */
    void send(Message message) throws RemoteException;
}
