package it.polimi.se2018.network.server;

import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.rmi.RMIServer;
import it.polimi.se2018.network.server.socket.SocketServer;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.HashMap;

public class Server {

    private static final int SOCKET_PORT = 1111;
    private static final int RMI_PORT = 1099;

    //private ArrayList<Lobby> lobbies;
    private HashMap<String, ClientInterface> clients;

    private SocketServer socketServer;
    private RMIServer rmiServer;

    public Server() throws RemoteException {
        this.clients = new HashMap<>();
        //this.lobbies = new ArrayList<Lobby>();
        this.socketServer = new SocketServer(this);
        this.rmiServer = new RMIServer(this);
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startServer(SOCKET_PORT, RMI_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startServer(int socketPort, int rmiPort) throws MalformedURLException, RemoteException {
        // Start both server
        socketServer.startServer(socketPort);
        socketServer.start();
        rmiServer.startServer(rmiPort);
    }

    public void addClient(String username, ClientInterface client) {
        // Login of the player
        // Client's type --> RMIClient or SocketClient
        clients.put(username, client);
    }

    public void sendToAll(String message) {
        // Broadcast message
        // For loop on all clients
        // Right now a simple print
        System.out.println(message);
    }
}
