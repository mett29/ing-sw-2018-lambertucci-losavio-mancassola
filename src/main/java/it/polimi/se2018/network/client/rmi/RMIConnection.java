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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class implements the logic of the RMI connection.
 * Implements {@link IConnection}
 * @author mett29
 */
public class RMIConnection implements IConnection {
    private ServerInterface server;
    private ClientInterface remoteClientRef;

    private static Logger logger = Logger.getLogger("rmiConnection");

    public RMIConnection(Client c) throws RemoteException, NotBoundException, MalformedURLException {
        server = (ServerInterface)Naming.lookup("Server");
        ClientImplementation client = new ClientImplementation(c);
        remoteClientRef = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);
    }

    /**
     * Once received the login from a player, it handles the action and demands it to the server
     * @param username, the client's username
     */
    public void registerClient(String username) {
        try {
            server.register(username, remoteClientRef);
        } catch (RemoteException e) {
            logger.log(Level.WARNING,e.getMessage());
        }
    }

    @Override
    public void send(Message message) throws RemoteException{
        server.send(message);
    }
}
