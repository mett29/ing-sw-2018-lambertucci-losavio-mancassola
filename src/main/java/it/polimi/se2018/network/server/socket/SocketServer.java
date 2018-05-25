package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.Message;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

    private ServerSocket serverSocket;
    private Server server;

    public SocketServer(Server server) {
        this.server = server;
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Socket server is on");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            Socket newClientConnection;
            try {
                newClientConnection = serverSocket.accept();
                VirtualClient virtualClient = new VirtualClient(this, newClientConnection);
                virtualClient.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void register(String username, ClientInterface client){
        server.addClient(username, client);
    }

    /**
     * Forward message to Server
     * This function is triggered when a message is received from a client (VirtualClient)
     * @param message Received message
     */
    public void onReceive(Message message) {
        server.onReceive(message);
    }
}
