package it.polimi.se2018.network.client.rmi;

import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.rmi.ServerInterface;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class RMIClient {

    public static void main(String[] args) {
        ServerInterface server;
        try {
            server = (ServerInterface)Naming.lookup("Server");
            ClientImplementation client = new ClientImplementation();
            ClientInterface remoteClientRef = (ClientInterface) UnicastRemoteObject.exportObject(client, 0);

            server.sendRequest("testing", remoteClientRef);

            Scanner scanner = new Scanner(System.in);
            boolean active = true;
            while(active){
                System.out.println("\nWrite a message:");
                String text = scanner.nextLine();
                server.send(text);
            }
            scanner.close();

        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
