package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.message.*;
import it.polimi.se2018.network.client.rmi.RMIConnection;
import it.polimi.se2018.network.client.socket.SocketConnection;
import it.polimi.se2018.view.cli.CLI;
import it.polimi.se2018.view.gui.GUI;
import it.polimi.se2018.view.ViewInterface;
import javafx.application.Application;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the main client class.
 * @author mett29, MicheleLambertucci
 */
public class Client {
    private boolean rmi;
    private String username;
    private IConnection connection;
    private ViewInterface view;

    private static String ipAddress;

    private static Logger logger = Logger.getLogger("client");

    public static String getIpAddress() {
        return ipAddress;
    }

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

    // If true run GUI
    private static boolean useFX = false;

    public static void main(String[] args) {
        if(args.length > 0){
            switch(args[0]){
                case "cli":
                    useFX = false;
                    break;
                case "gui":
                    useFX = true;
                    break;
                default:
                    printUsageMessage();
            }
        }
        if(args.length > 1){
            Client.ipAddress = args[1];
        } else {
            Client.ipAddress = "localhost";
        }
        if(useFX) {
            Application.launch(GUI.class, args);
        } else {
            Client client = new Client();
            CLI view = new CLI(client);
            client.setView(view);

            view.launch();
        }
    }

    private static void printUsageMessage() {
        logger.log(Level.INFO, "Usage: \n\tsagrada.exe [cli/gui] [ip_address]\n");
    }

    /**
     * This method creates the connection according to the user's choice
     * {@link RMIConnection}{@link SocketConnection}
     */
    public void connect() throws RemoteException, NotBoundException, MalformedURLException {
        if(rmi){
            connection = new RMIConnection(this);
            connection.registerClient(username);
        } else {
            connection = new SocketConnection(this);
            connection.send(new LoginRequest(username));
        }
    }

    /**
     * This method sends the user's move using the previously created connection
     * @param move, the client's move
     */
    public void sendMove(ClientMove move) {
        try {
            connection.send(new MoveMessage(username, move));
        } catch (RemoteException e) {
            view.displayConnectionError(e);
        }
    }

    /**
     * According to the type of message, this method notifies the View (CLI/GUI)
     * @param message object to read
     */
    public void notify(Message message){
        switch(message.content){
            case LOGIN:
                view.onConnect(((LoginResponse) message).response == Message.Type.OK);
                break;

            case TOOLCARD_RESPONSE:
                view.displayToolcardActivationResponse(((ToolCardResponse) message).response == Message.Type.OK);
                break;

            case MATCH_STATE:
                view.updateMatch(((MatchStateMessage) message).payload);
                break;

            case MATCH_START:
                Match match = ((MatchStartMessage) message).payload;
                int timerValue = ((MatchStartMessage) message).timerValue;
                view.onMatchStart(match, timerValue);
                break;

            case PATTERN_REQUEST:
                view.askPattern((PatternRequest) message);
                break;

            case UNDO_RESPONSE:
                view.displayUndoMessage((UndoResponse) message);
                break;

            case TIME_RESET:
                view.resetTimer();
                break;

            default:
                // Strange message received. This shouldn't happen
                logger.log(Level.INFO,"Strange and stranger things might happen while programming");
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
        view.displayConnectionError(e);
    }

    public void setView(ViewInterface view) {
        this.view = view;
    }

    /**
     * This method sends a ToolCard activation request to the server
     * @param index, index of the toolcard
     */
    public void activateToolCard(int index) {
        Message message = new ToolCardRequest(username, index);
        try {
            connection.send(message);
        } catch (Exception e) {
            view.displayConnectionError(e);
        }
    }

    /**
     * This method sends a Normal Move activation request to the server
     */
    public void activateNormalMove(){
        Message message = new NormalMoveRequest(username);
        try {
            connection.send(message);
        } catch (RemoteException e) {
            view.displayConnectionError(e);
        }
    }

    /**
     * This method sends a PatternResponse message to the server
     * @param index, index of the pattern chosen by the player
     */
    public void sendPatternResponse(int index) {
        Message message = new PatternResponse(username, index);
        try {
            connection.send(message);
        } catch(Exception e){
            view.displayConnectionError(e);
        }
    }

    /**
     * Send to the server the desire of passing the turn
     */
    public void pass() {
        Message message = new PassRequest(username);
        try {
            connection.send(message);
        } catch (RemoteException e) {
            view.displayConnectionError(e);
        }
    }

    /**
     * Send to the server the desire of undoing the just took action
     */
    public void sendUndoRequest() {
        try {
            connection.send(new UndoRequest(username));
        } catch (RemoteException e) {
            view.displayConnectionError(e);
        }
    }
}
