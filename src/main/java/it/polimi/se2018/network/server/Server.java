package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Configuration;
import it.polimi.se2018.model.EnumState;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.network.client.QueueRequest;
import it.polimi.se2018.network.message.*;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.rmi.RMIServer;
import it.polimi.se2018.network.server.socket.SocketServer;

import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is the main Server class.
 * It runs both RMI and Socket servers and it handles lobbies and clients' connection
 * @author mett29, MicheleLambertucci, ontech7
 */
public class Server {
    private static final int MAX_PLAYER_NUMBER = 2; //TODO: change to 4 before deployment

    private PlayerQueue queue;

    private Map<String, Client> usernames;
    private Map<String, Lobby> lobbies;

    private SocketServer socketServer;
    private RMIServer rmiServer;

    private static Logger logger = Logger.getLogger("server");

    private Server() {
        this.queue = new PlayerQueue(MAX_PLAYER_NUMBER, this);

        this.usernames = new HashMap<>();
        this.lobbies = new HashMap<>();

        this.socketServer = new SocketServer(this);
        this.rmiServer = new RMIServer(this);
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startServer(Configuration.getInstance().getSocketPort(), Configuration.getInstance().getRmiPort());
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Start both RMI and Socket server
     * @param socketPort Port of the socket server
     * @param rmiPort Port of the RMI server
     * @throws RemoteException
     */
    private void startServer(int socketPort, int rmiPort) throws RemoteException {
        socketServer.startServer(socketPort);
        socketServer.start();
        rmiServer.startServer(rmiPort);
    }

    /**
     * Creates a new lobby based on players that were waiting in the queue
     * @param players object to set
     */
    void newLobby(List<String> players)  {
        Lobby lobby = new Lobby(players, this);
        for (String username : players) {
            lobbies.put(username, lobby);
        }
        lobby.startMatch();
    }

    /**
     * Reconnects the client to the current match if he disconnected or lost connection
     * @param username object to read
     * @param clientInterface object to read
     */
    private void reconnectClient(String username, ClientInterface clientInterface) {
        Client client = usernames.get(username);
        client.setClientInterface(clientInterface);
        client.setState(Client.State.CONNECTED);
        Player player = lobbies.get(username).getMatch().getPlayerByName(username);
        try {
            player.setDisconnected(false);
            clientInterface.notify(new LoginResponse(true, lobbies.get(username).getMatch()));
        } catch (NullPointerException|RemoteException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    /**
     * Adds a new client into the server client list in case of successful login
     * Reconnects to the match if the state is DISCONNECTED
     * @param username object to read/set
     * @param clientInterface object to read/set
     */
    public void addClient(String username, ClientInterface clientInterface) {
        // Login of the player
        if(lobbies.containsKey(username) && usernames.get(username).getState() == Client.State.CONNECTED) {
            try {
                clientInterface.notify(new LoginResponse(false, null));
            } catch (RemoteException e1) {
                logger.log(Level.WARNING, e1.getMessage());
            }
        }
        else if(lobbies.containsKey(username) && usernames.get(username).getState() == Client.State.DISCONNECTED) {
            reconnectClient(username, clientInterface);
        } else {
            try {
                Client client = new Client(username, clientInterface);
                if (usernames.containsKey(username)) throw new InvalidUsernameException();
                usernames.put(username, client);
                clientInterface.notify(new LoginResponse(true, null));
            } catch (InvalidUsernameException e) {
                try {
                    clientInterface.notify(new LoginResponse(false, null));
                } catch (RemoteException e1) {
                    logger.log(Level.WARNING, e1.getMessage());
                }
            } catch (RemoteException e) {
                logger.log(Level.WARNING, e.getMessage());
                onDisconnect(username);
            }
        }
        logger.log(Level.INFO, "{0}", usernames);
    }

    /**
     * Disconnets the client from the match
     * @param username object to read/remove
     */
    public void onDisconnect(String username) {
        if(!lobbies.containsKey(username)) {
            queue.remove(username);
            usernames.remove(username);
        } else if(usernames.containsKey(username)){
            usernames.get(username).setState(Client.State.DISCONNECTED);
            Player player = lobbies.get(username).getMatch().getPlayerByName(username);
            try {
                player.setDisconnected(true);
                if(player.getBoard() == null)
                    onReceive(new PatternResponse(player.getName(), 0));
                while(player.getState().get() == EnumState.YOUR_TURN) {
                    onReceive(new PassRequest(player.getName()));
                }
                if(usernames.containsKey(username))
                    while(lobbies.get(username).getMatch().getPlayerQueue().remove(player));
            } catch (NullPointerException e) {
                logger.log(Level.WARNING, e.getMessage());
            }
        }
    }

    /**
     * Sends a message to the player's client
     * @param username object to read
     * @param message object to send
     */
    public void send(String username, Message message){
        if(usernames.containsKey(username) && usernames.get(username).getState() == Client.State.CONNECTED) {
            usernames.get(username).notify(message);
        } else {
            onDisconnect(username);
        }
    }

    /**
     * Forward message to player's lobby
     * Function called when a client sends a message.
     * @param message Message received
     */
    public void onReceive(Message message)  {
        logger.log(Level.INFO, "{0}", message);
        if(lobbies.containsKey(message.username)){
            lobbies.get(message.username).onReceive(message);
        } else if(usernames.containsKey(message.username) && message.content == Message.Content.QUEUE) {
            handleQueueRequest((QueueRequest) message);
        } else if(!lobbies.containsKey(message.username)) {
            //do nothing
        } else {
            logger.log(Level.WARNING, "Unhandled message received from: {0}", message.username);
            // Ignore message received from unknown client
            // He needs to re-register
        }
    }

    /**
     * Add new player to the queue.
     * @param message Message received from player who whats to connect
     */
    private void handleQueueRequest(QueueRequest message) {
        queue.add(message.username);
    }

    void deleteLobbyByPlayerNames(List<Player> players) {
        players.forEach(player -> {
            lobbies.remove(player.getName());
            usernames.remove(player.getName());
        });
    }
}
