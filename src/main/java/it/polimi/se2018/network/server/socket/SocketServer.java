package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer extends Thread {

    private static ServerSocket serverSocket;
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

        //System.out.println("Waiting for clients...\n");

        while(true) {
            Socket newClientConnection;
            try {
                newClientConnection = serverSocket.accept();
                System.out.println("A new client connected.");
                VirtualClient virtualClient = new VirtualClient(this, newClientConnection);
                server.addClient("username", virtualClient);
                virtualClient.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String message) {
        server.sendToAll(message);
    }
}
