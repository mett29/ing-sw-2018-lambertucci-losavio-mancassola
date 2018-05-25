package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.Message;
import it.polimi.se2018.network.client.socket.SocketClient;

import java.io.*;
import java.net.Socket;

public class VirtualClient extends Thread implements SocketClient {

    private final SocketServer server;
    private Socket clientConnection;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public VirtualClient(SocketServer server, Socket clientConnection) {
        this.server = server;
        this.clientConnection = clientConnection;
        try {
            this.ois = new ObjectInputStream(clientConnection.getInputStream());
            this.oos = new ObjectOutputStream(clientConnection.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
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
                    System.out.println("Holy shit received!");

                } else {
                    System.out.println("Message received!");
                    if(message.content.equals("login")){
                        server.register(message.username, this);
                    } else {
                        server.onReceive(message);
                    }
                }
            }

        } catch (IOException|ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void notify(Message message) {
        try {
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
