package it.polimi.se2018.network.server;

import it.polimi.se2018.network.message.LoginResponse;
import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.ClientInterface;
import it.polimi.se2018.network.server.rmi.RMIServer;
import it.polimi.se2018.network.server.socket.SocketServer;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {

    private static final int SOCKET_PORT = 1111;
    private static final int RMI_PORT = 1099;

    private List<String> queue;
    private Map<String, Client> usernames;

    private SocketServer socketServer;
    private RMIServer rmiServer;

    private Server() throws RemoteException {
        this.queue = new ArrayList<>();
        this.usernames = new HashMap<>();

        this.socketServer = new SocketServer(this);
        this.rmiServer = new RMIServer(this);
    }

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startServer(SOCKET_PORT, RMI_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start both RMI and Socket server
     * @param socketPort Port of the socket server
     * @param rmiPort Port of the RMI server
     * @throws MalformedURLException
     * @throws RemoteException
     */
    private void startServer(int socketPort, int rmiPort) throws MalformedURLException, RemoteException {
        socketServer.startServer(socketPort);
        socketServer.start();
        rmiServer.startServer(rmiPort);
    }

    public void addClient(String username, ClientInterface clientInterface) {
        // Login of the player
        try {
            Client client = new Client(username, clientInterface);
            if(usernames.containsKey(username)) throw new InvalidUsernameException();
            usernames.put(username, client);
            queue.add(username);
            clientInterface.notify(new LoginResponse(true));
        } catch (InvalidUsernameException e) {
            try {
                clientInterface.notify(new LoginResponse(false));
            } catch (RemoteException e1) {
                //Client disconnected before registering
            }
        } catch (RemoteException e) {
            //Client disconnected while server was notifying ok
            onDisconnect(username);
        }

        System.out.println(queue);
        System.out.println(usernames);
    }

    public void onDisconnect(String username){
        //TODO: If player is not in match, remove from queue and map. Otherwise set it to DISCONNECTED.
        if(usernames.containsKey(username)){
            usernames.get(username).setState(Client.State.DISCONNECTED);
        }
    }

    /**
     * Forward message to player's lobby
     * Function called when a client sends a message.
     * @param message Message received
     */
    public void onReceive(Message message){
        System.out.println(message.username + ": " + message.content);
    }
}
