package it.polimi.se2018.network.server;

import it.polimi.se2018.network.Message;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.rmi.RMIServer;
import it.polimi.se2018.network.server.socket.SocketServer;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    private static final int SOCKET_PORT = 1111;
    private static final int RMI_PORT = 1099;

//    private HashMap<String, ClientInterface> clients;

    private ArrayList<ClientInterface> clients;

    private SocketServer socketServer;
    private RMIServer rmiServer;

    public Server() throws RemoteException {
//        this.clients = new HashMap<>();
        this.clients = new ArrayList<>();

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

    /**
     * Start both RMI and Socket server
     * @param socketPort Port of the socket server
     * @param rmiPort Port of the RMI server
     * @throws MalformedURLException
     * @throws RemoteException
     */
    private void startServer(int socketPort, int rmiPort) throws MalformedURLException, RemoteException {
        socketServer.startServer(socketPort);
        socketServer.start();
        rmiServer.startServer(rmiPort);
    }

    public void addClient(String username, ClientInterface client) {
        // Login of the player
        // Client's type --> RMIConnection or SocketClient
        clients.add(client);
        System.out.println(username + " has joined");
    }

    /**
     * Function called when a client sends a message
     * @param message Message received
     */
    public void onReceive(Message message){
        System.out.println(message.username + ": " + message.content);
    }
}
