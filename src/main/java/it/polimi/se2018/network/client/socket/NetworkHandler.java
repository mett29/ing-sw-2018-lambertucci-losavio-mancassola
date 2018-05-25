package it.polimi.se2018.network.client.socket;

import it.polimi.se2018.network.Message;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.socket.ServerInterface;

import java.io.*;
import java.net.Socket;

public class NetworkHandler extends Thread implements ServerInterface {

    private Socket socketClient;

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    private ClientInterface client;

    NetworkHandler(String host, int port, ClientInterface client) {
        try {
            this.socketClient = new Socket(host, port);
            this.oos = new ObjectOutputStream(new BufferedOutputStream(socketClient.getOutputStream()));
            this.oos.flush();
            this.ois = new ObjectInputStream(new BufferedInputStream(socketClient.getInputStream()));
            this.client = client;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean loop = true;
        while (loop && !this.socketClient.isClosed()) {
            try {
                Message message = (Message) ois.readObject();
                if (message == null) {
                    loop = false;
                    //this.stopConnection();
                } else {
                    client.notify(message);
                }
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send a message to the server
     * @param message Message to be sent
     */
    public synchronized void send(Message message) {
        try {
            oos.writeObject(message);
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
