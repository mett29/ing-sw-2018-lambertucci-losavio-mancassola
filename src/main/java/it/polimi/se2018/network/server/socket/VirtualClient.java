package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.message.LoginRequest;
import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.socket.SocketClient;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents the logic of the client.
 * A new VirtualClient is created whenever SocketServer receives a new connection request.
 * @author mett29
 */
public class VirtualClient extends Thread implements SocketClient {

    private final SocketServer server;
    private String username;
    private Socket clientConnection;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private static Logger logger = Logger.getLogger("virtualClient");

    public VirtualClient(SocketServer server, Socket clientConnection) {
        this.server = server;
        this.clientConnection = clientConnection;
        try {
            this.ois = new ObjectInputStream(this.clientConnection.getInputStream());
            this.oos = new ObjectOutputStream(this.clientConnection.getOutputStream());
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            boolean loop = true;
            while(loop) {
                //When a message is received, forward to Server
                Message message = (Message) ois.readObject();
                if (message == null) {
                    loop = false;
                } else {
                    if(message.content == Message.Content.LOGIN && ((LoginRequest) message).type == Message.Type.REQUEST) {
                        this.username = message.username;
                        server.register(message.username, this);
                        server.onReceive(message);
                    } else {
                        server.onReceive(message);
                    }
                }
            }
        } catch (IOException|ClassNotFoundException e) {
            server.onDisconnect(username);
        }
    }

    @Override
    public void notify(Message message) {
        try {
            oos.reset();
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            logger.log(Level.WARNING,e.getMessage());
        }
    }
}
