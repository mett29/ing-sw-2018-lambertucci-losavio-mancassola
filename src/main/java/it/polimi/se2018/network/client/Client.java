package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.message.*;
import it.polimi.se2018.network.client.rmi.RMIConnection;
import it.polimi.se2018.network.client.socket.SocketConnection;
import it.polimi.se2018.view.CLI;
import it.polimi.se2018.view.GUI.GUI;
import it.polimi.se2018.view.ViewInterface;
import javafx.application.Application;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {
    private boolean rmi;
    private String username;
    private IConnection connection;
    private ViewInterface view;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setConnection(boolean isRmi){
        rmi = isRmi;
    }

    public boolean isRmi(){
        return rmi;
    }

    public Client(){
        rmi = false;
    }

    private static boolean useFX = true;

    public static void main(String[] args) {
        if(useFX) {
            Application.launch(GUI.class, args);
        } else {
            Client client = new Client();
            ViewInterface view = new CLI(client);
            client.setView(view);

            view.askLogin();
            view.askTypeOfConnection();

            try {
                client.connect();
                view.waitFor();
            } catch (Exception e) {
                view.onConnectionError(e);
            }
        }
    }

    public void connect() throws RemoteException, NotBoundException, MalformedURLException {
        if(rmi){
            connection = new RMIConnection(this, username);
        } else {
            connection = new SocketConnection(this, username);
        }
    }

    public void sendMove(ClientMove move) {
        try {
            connection.send(new MoveMessage(username, move));
        } catch (RemoteException e) {
            view.onConnectionError(e);
        }
    }

    public void notify(Message message){
        switch(message.content){
            case LOGIN:
                view.onConnect();
                break;

            case TOOLCARD_RESPONSE:
                view.onToolCardActivationResponse(((ToolCardResponse) message).response == Message.Type.OK);
                break;

            case MATCH_STATE:
                view.updateMatch(((MatchStateMessage) message).payload);
                break;

            case MATCH_START:
                Match match = ((MatchStartMessage) message).payload;
                view.onMatchStart(match);
                break;

            case PATTERN_REQUEST:
                view.onPatternRequest((PatternRequest) message);
                break;

            default:
                // Strange message received. This shouldn't happen
                System.out.println("Strange and stranger things might happen while programming");
        }
    }

    /**
     * Send a QueueRequest to the server.
     * Communicate to the server that this client wants to play
     * @param playerNumber Type of queue (desired number of players in match)
     */
    public void sendQueueRequest(int playerNumber) {
        try {
            connection.send(new QueueRequest(username, playerNumber));
        } catch(Exception e){
            onConnectionError(e);
        }
    }

    public void onConnectionError(Exception e){
        view.onConnectionError(e);
    }

    public void setView(ViewInterface view) {
        this.view = view;
    }

    public void activateToolCard(int index) {
        Message message = new ToolCardRequest(username, index);
        try {
            connection.send(message);
        } catch (Exception e) {
            view.onConnectionError(e);
        }
    }
}
