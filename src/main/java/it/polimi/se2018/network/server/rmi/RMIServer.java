package it.polimi.se2018.network.server.rmi;

import it.polimi.se2018.network.server.Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the RMI server, started by the main Server class in the start-up phase.
 *
 * @author mett29
 */
public class RMIServer implements Remote {

    private Server server;

    private static Logger logger = Logger.getLogger("rmiServer");

    public RMIServer(Server server) {
        this.server = server;
    }

    // Start the server and do the rebind to tell the Registry I have a new service to offer to the clients
    public void startServer(int port) throws RemoteException {
        LocateRegistry.createRegistry(port);
        ServerImplementation serverImplementation = new ServerImplementation(server);
        try {
            Naming.rebind("//localhost/Server", serverImplementation);
            logger.log(Level.INFO,"RMI server is on");
        } catch(RemoteException | MalformedURLException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }
}
