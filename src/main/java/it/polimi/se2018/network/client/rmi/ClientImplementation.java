package it.polimi.se2018.network.client.rmi;

import it.polimi.se2018.network.client.ClientInterface;

import java.rmi.RemoteException;

public class ClientImplementation implements ClientInterface {

    public void notify(String message) throws RemoteException {
        System.out.println("Received: " + message);
    }

}
