package it.polimi.se2018.network.server.socket;

import it.polimi.se2018.network.client.socket.SocketClient;

import java.io.*;
import java.net.Socket;

public class VirtualClient extends Thread implements SocketClient {

    private final SocketServer server;
    private Socket clientConnection;

    private InputStream inputStream;
    private OutputStream outputStream;

    public VirtualClient(SocketServer server, Socket clientConnection) {
        this.server = server;
        this.clientConnection = clientConnection;
        try {
            this.inputStream = clientConnection.getInputStream();
            this.outputStream = clientConnection.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader bufIn = new BufferedReader(new InputStreamReader(inputStream));
            boolean loop = true;
            while(loop) {
                //System.out.println("Waiting for messages...");
                String message = bufIn.readLine();
                if (message == null) {
                    loop = false;
                } else {
                    server.send(message);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notify(String message) {
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
