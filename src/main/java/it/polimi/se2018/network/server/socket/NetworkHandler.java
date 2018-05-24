package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.client.ClientInterface;

import java.io.*;
import java.net.Socket;

public class NetworkHandler extends Thread implements ServerInterface {

    private Socket socketClient;

    private InputStream inputStream;
    private OutputStream outputStream;

    private ClientInterface client;

    public NetworkHandler(String host, int port, ClientInterface client) {
        try {
            this.socketClient = new Socket(host, port);
            System.out.println("Socket connesso");
            this.inputStream = socketClient.getInputStream();
            this.outputStream = socketClient.getOutputStream();
            this.client = client;
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Listening for messages from the Server...");
        BufferedReader bufIn = new BufferedReader(new InputStreamReader(inputStream));
        boolean loop = true;
        while (loop && !this.socketClient.isClosed()) {
            try {
                String message = bufIn.readLine();
                if (message == null) {
                    loop = false;
                    //this.stopConnection();
                } else {
                    client.notify(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void send(String message) {
        BufferedWriter bufOut = new BufferedWriter(new OutputStreamWriter(outputStream));
        try {
            bufOut.write(message);
            bufOut.newLine();
            bufOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
