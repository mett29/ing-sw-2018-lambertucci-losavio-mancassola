package it.polimi.se2018.network.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    public void notify(String message) throws RemoteException;

}
