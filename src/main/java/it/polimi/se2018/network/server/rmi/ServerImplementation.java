package it.polimi.se2018.network.server.rmi;

import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.Server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

    private Server server;

    protected ServerImplementation(Server server) throws RemoteException {
        this.server = server;
    }
    // Method used by RMIConnection to do the login operation
    @Override
    public void register(String username, ClientInterface client) {
        server.addClient(username, client);
    }

    @Override
    public void send(Message message) {
        server.onReceive(message);
    }
}
