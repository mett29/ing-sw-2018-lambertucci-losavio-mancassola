package it.polimi.se2018.network.server.rmi;

import it.polimi.se2018.network.server.Server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIServer implements Remote {

    private Server server;

    public RMIServer(Server server) {
        this.server = server;
    }

    public void startServer(int port) throws RemoteException {
        LocateRegistry.createRegistry(port);
        ServerImplementation serverImplementation = new ServerImplementation(server);
        try {
            Naming.rebind("//localhost/Server", serverImplementation);
            System.out.println("RMI server is on");
        } catch(RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
