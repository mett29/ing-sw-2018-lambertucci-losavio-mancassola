package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Configuration;
import it.polimi.se2018.model.EnumState;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.network.client.QueueRequest;
import it.polimi.se2018.network.message.*;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.rmi.RMIServer;
import it.polimi.se2018.network.server.socket.SocketServer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.*;

public class Server {

    private static final int MAX_PLAYER_NUMBER = 3; //TODO: change to 4 before deployment

    private PlayerQueue queue;

    private Map<String, Client> usernames;
    private Map<String, Lobby> lobbies;

    private SocketServer socketServer;
    private RMIServer rmiServer;

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
            e.printStackTrace();
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

    void newLobby(List<String> players) throws IOException {
        Lobby lobby = new Lobby(players, this);
        for (String username : players) {
            lobbies.put(username, lobby);
        }
        lobby.startMatch();
    }

    public void addClient(String username, ClientInterface clientInterface) {
        // Login of the player
        if(lobbies.containsKey(username) && usernames.get(username).getState() == Client.State.CONNECTED) {
            try {
                clientInterface.notify(new LoginResponse(false, null));
            } catch (RemoteException e1) {
                //Client disconnected before registering
            }
        }
        else if(lobbies.containsKey(username) && usernames.get(username).getState() == Client.State.DISCONNECTED) {
            Client client = usernames.get(username);
            client.setClientInterface(clientInterface);
            client.setState(Client.State.CONNECTED);
            Player player = lobbies.get(username).getMatch().getPlayerByName(username);
            try {
                player.setDisconnected(false);
                clientInterface.notify(new LoginResponse(true, lobbies.get(username).getMatch()));
            } catch (NullPointerException|RemoteException e) {
                e.printStackTrace();
            }
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
                    //Client disconnected before registering
                }
            } catch (RemoteException e) {
                //Client disconnected while server was notifying ok
                try {
                    onDisconnect(username);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        System.out.println(usernames);
    }

    public void onDisconnect(String username) throws IOException{
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
                if(player.getState().get() == EnumState.YOUR_TURN)
                    onReceive(new PassRequest(player.getName()));
                if(lobbies.containsKey(username))
                    while(lobbies.get(username).getMatch().getPlayerQueue().remove(player));
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String username, Message message){
        if(usernames.containsKey(username) && usernames.get(username).getState() == Client.State.CONNECTED)
            usernames.get(username).notify(message);
    }

    /**
     * Forward message to player's lobby
     * Function called when a client sends a message.
     * @param message Message received
     */
    public void onReceive(Message message) throws IOException {
        System.out.println(message);
        if(lobbies.containsKey(message.username)){
            lobbies.get(message.username).onReceive(message);
        } else if(usernames.containsKey(message.username) && message.content == Message.Content.QUEUE) {
            handleQueueRequest((QueueRequest) message);
        } else if(!lobbies.containsKey(message.username)) {
            //do nothing
        } else {
            System.err.println("Unhandled message received from: " + message.username);
            // Ignore message received from unknown client
            // He needs to re-register
        }
    }

    /**
     * Add new player to the queue.
     * @param message Message received from player who whats to connect
     */
    private void handleQueueRequest(QueueRequest message) throws IOException {
        queue.add(message.username);
    }

    void deleteLobbyByPlayerNames(List<Player> players) {
        players.forEach(player -> {
            lobbies.remove(player.getName());
            usernames.remove(player.getName());
        });
    }
}
