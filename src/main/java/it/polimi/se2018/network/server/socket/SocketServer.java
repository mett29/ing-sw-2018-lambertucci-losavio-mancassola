package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the Socket server, run by the main Server class in the start-up phase
 * @author mett29
 */
public class SocketServer extends Thread {

    private ServerSocket serverSocket;
    private Server server;

    private static Logger logger = Logger.getLogger("socketServer");

    public SocketServer(Server server) {
        this.server = server;
    }

    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            logger.log(Level.INFO,"Socket server is on");
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        }
    }

    @Override
    public void run() {
        while(true) {
            Socket newClientConnection;
            try {
                newClientConnection = serverSocket.accept();
                (new VirtualClient(this, newClientConnection)).start();
            } catch (IOException e) {
                logger.log(Level.WARNING,e.getMessage());
            }
        }
    }

    void register(String username, ClientInterface client){
        server.addClient(username, client);
    }

    void onDisconnect(String username){
        server.onDisconnect(username);
    }

    /**
     * Forward message to Server
     * This function is triggered when a message is received from a client (VirtualClient)
     * @param message Received message
     * @throws IOException exception of input/output
     */
    void onReceive(Message message) throws IOException {
        server.onReceive(message);
    }
}
