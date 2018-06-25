package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.message.*;
import it.polimi.se2018.network.client.rmi.RMIConnection;
import it.polimi.se2018.network.client.socket.SocketConnection;
import it.polimi.se2018.view.CLI.CLI;
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

    private static boolean useFX = false;

    public static void main(String[] args) {
        if(useFX) {
            Application.launch(GUI.class, args);
        } else {
            Client client = new Client();
            CLI view = new CLI(client);
            client.setView(view);

            view.launch();
        }
    }

    public void connect() throws RemoteException, NotBoundException, MalformedURLException {
        if(rmi){
            connection = new RMIConnection(this, username);
            connection.registerClient(username);
        } else {
            connection = new SocketConnection(this, username);
            connection.send(new LoginRequest(username));
        }
    }

    public void reconnect() throws RemoteException, NotBoundException, MalformedURLException {
        if(rmi){
            connection = new RMIConnection(this, username);
            connection.registerClient(username);
        } else {
            connection = new SocketConnection(this, username);
            connection.send(new ReconnectRequest(username));
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

            case RECONNECT:
                view.onReconnect(((ReconnectResponse) message).payload);
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

            case UNDO_RESPONSE:
                view.onUndoResponse((UndoResponse) message);
                break;

            default:
                // Strange message received. This shouldn't happen
                System.out.println("Strange and stranger things might happen while programming");
        }
    }

    /**
     * Send a QueueRequest to the server.
     * Communicate to the server that this client wants to play
     */
    public void sendQueueRequest() {
        try {
            connection.send(new QueueRequest(username));
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

    public void activateNormalMove(){
        Message message = new NormalMoveRequest(username);
        try {
            connection.send(message);
        } catch (RemoteException e) {
            view.onConnectionError(e);
        }
    }

    public void sendPatternResponse(int index) {
        Message message = new PatternResponse(username, index);
        try {
            connection.send(message);
        } catch(Exception e){
            view.onConnectionError(e);
        }
    }

    public void pass() {
        Message message = new PassRequest(username);
        try {
            connection.send(message);
        } catch (RemoteException e) {
            view.onConnectionError(e);
        }
    }

    public void sendUndoRequest() {
        try {
            connection.send(new UndoRequest(username));
        } catch (RemoteException e) {
            view.onConnectionError(e);
        }
    }
}
