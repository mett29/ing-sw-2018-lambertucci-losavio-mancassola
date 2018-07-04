package it.polimi.se2018.network.client.rmi;

import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.client.ClientInterface;

import java.rmi.RemoteException;

public class ClientImplementation implements ClientInterface {
    private Client client;
    ClientImplementation(Client client){
        this.client = client;
    }

    @Override
    public void notify(Message message) throws RemoteException {
        (new Thread(() -> client.notify(message))).start();
    }

}
