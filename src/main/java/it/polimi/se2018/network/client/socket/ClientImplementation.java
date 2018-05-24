package it.polimi.se2018.network.client.socket;

import it.polimi.se2018.network.server.socket.NetworkHandler;
import it.polimi.se2018.network.server.socket.ServerInterface;

import java.util.Scanner;

public class ClientImplementation implements SocketClient {

    private static final int PORT = 1111;
    private static final String HOST = "localhost";

    public static void main(String[] args) {
        ServerInterface server = new NetworkHandler(HOST, PORT, new ClientImplementation());

        Scanner scanner = new Scanner(System.in);

        boolean loop = true;
        while (loop) {
            System.out.println("\nWrite a message: ");
            String text = scanner.nextLine();

            if (text.equals("stop"))  {
                scanner.close();
                loop = false;
            } else {
                server.send(text);
            }
        }
    }

    @Override
    public void notify(String message) {
        System.out.println("Received: " + message);
    }
}
