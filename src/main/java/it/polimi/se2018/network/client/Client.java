package it.polimi.se2018.network.client;

import it.polimi.se2018.network.message.LoginResponse;
import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.rmi.RMIConnection;
import it.polimi.se2018.network.client.socket.SocketConnection;
import it.polimi.se2018.network.message.MoveMessage;
import it.polimi.se2018.network.message.ToolCardResponse;

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

        client.username = "Giovanni";
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

    private void sendPick(ClientMove move) throws RemoteException {
        connection.send(new MoveMessage(username, move));
    }

    public void notify(Message message){
        switch(message.content){
            case LOGIN:
                if(((LoginResponse) message).response == Message.Type.FAILURE){
                    // display no connection
                    System.out.println("Not able to login. Change username?");
                }
                break;

            case TOOLCARD_RESPONSE:
                // display toolcard activation status
                System.out.println("ToolCard Activation status: " + ((ToolCardResponse) message).response);
                break;

            case MATCH_STATE:
                // update view with new model state
                System.out.println("New match state received");
                break;

            default:
                // Strange message received. This shouldn't happen
                System.out.println("Strange and stranger things might happen while programming");
        }
    }
}
