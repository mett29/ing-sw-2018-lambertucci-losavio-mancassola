package it.polimi.se2018.network.client.rmi;

import it.polimi.se2018.network.message.Message;
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
    private ClientInterface remoteClientRef;
    public RMIConnection(Client c, String username) throws RemoteException, NotBoundException, MalformedURLException {
        server = (ServerInterface)Naming.lookup("Server");
        ClientImplementation client = new ClientImplementation(c);
        remoteClientRef = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);
    }

    public void registerClient(String username) {
        try {
            server.register(username, remoteClientRef);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(Message message) throws RemoteException{
        server.send(message);
    }
}
