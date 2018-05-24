package it.polimi.se2018.network.server.rmi;

import it.polimi.se2018.network.client.ClientInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {

    void sendRequest(String message, ClientInterface client) throws RemoteException;

    void send(String message) throws RemoteException;
}
