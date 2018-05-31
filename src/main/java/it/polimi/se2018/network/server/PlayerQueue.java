package it.polimi.se2018.network.server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Represents a queue of players waiting for a match.
 * When enough players are added to a queue, a new lobby is created.
 * @author MicheleLambertucci
 */
public class PlayerQueue {
    private int numberOfPlayers;
    private Queue<String> queue;
    private Server server;

    /**
     * Constructor
     * @param numberOfPlayers Number of players for the matches that will be created by this queue
     * @param server Reference to the object. It will be used to call `newLobby`
     */
    public PlayerQueue(int numberOfPlayers, Server server){
        this.numberOfPlayers = numberOfPlayers;
        queue = new LinkedList<>();
        this.server = server;
    }

    /**
     * Add a new player to the queue.
     * If players are enough, a new Lobby will be automatically added to server's lobbies
     * @param username Username of the player to be added
     */
    public void add(String username){
        queue.add(username);
        checkAndCreate();
    }

    /**
     * Check if players are enough to create a new lobby and creates it
     */
    private void checkAndCreate(){
        while(canSpawnLobby()){
            List<String> players = new ArrayList<>();
            for (int i = 0; i < numberOfPlayers; i++) {
                players.add(queue.poll());
            }
            server.newLobby(players);
        }
    }

    private boolean canSpawnLobby(){
        return queue.size() >= numberOfPlayers;
    }
}
