package it.polimi.se2018.network.client.rmi;

import it.polimi.se2018.network.Message;
import it.polimi.se2018.network.client.Client;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.client.IConnection;
import it.polimi.se2018.network.server.rmi.ServerInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIConnection implements IConnection {
    private ServerInterface server;
    public RMIConnection(Client c, String username) throws RemoteException, NotBoundException, MalformedURLException {
        server = (ServerInterface)Naming.lookup("Server");
        ClientImplementation client = new ClientImplementation(c);
        ClientInterface remoteClientRef = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);

        server.register(username, remoteClientRef);
    }

    @Override
    public void send(Message message) throws RemoteException{
        server.send(message);
    }
}
