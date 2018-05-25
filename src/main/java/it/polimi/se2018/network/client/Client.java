package it.polimi.se2018.network.client;

import it.polimi.se2018.network.Message;
import it.polimi.se2018.network.client.rmi.RMIConnection;
import it.polimi.se2018.network.client.socket.SocketConnection;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    private boolean rmi;
    private String username;
    private IConnection connection;

    private Client(){
        rmi = false;
    }

    public static void main(String[] args){
        Client client = new Client();

        //TODO: Start view

        client.username = "Giorgio";
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() throws RemoteException, NotBoundException, MalformedURLException {
        if(rmi){
            connection = new RMIConnection(this, username);
        } else {
            connection = new SocketConnection(this, username);
        }
    }

    public void notify(Message message){
        System.out.println("Received message:");
        System.out.println(message.content);

        //TODO: handle incoming message
    }
}
