package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Configuration;

import java.util.*;

/**
 * Represents a queue of players waiting for a match.
 * When enough players are added to a queue, a new lobby is created.
 * @author MicheleLambertucci
 */
public class PlayerQueue {
    private int numberOfPlayers;
    private Queue<String> queue;
    private Server server;
    private Timer timer;

    /**
     * Constructor
     * @param numberOfPlayers Number of players for the matches that will be created by this queue
     * @param server Reference to the object. It will be used to call `newLobby`
     */
    PlayerQueue(int numberOfPlayers, Server server){
        this.numberOfPlayers = numberOfPlayers;
        queue = new LinkedList<>();
        this.server = server;
    }

    /**
     * Add a new player to the queue.
     * If players are enough, a new Lobby will be automatically added to server's lobbies.
     * If queue is empty before adding (this is the first player to enter the lobby), start lobby timer
     * @param username Username of the player to be added
     */
    public synchronized void add(String username){
        // Start timer if this is the first player
        if(queue.isEmpty()) {
            timer = new Timer();
            startTimer();
        }

        queue.add(username);

        System.out.println("Queue: " + queue);

        // If the number of player wanted is reached, spawn a new lobby
        // "reset" the timer to avoid any interference while handling future players
        if(queueMaxSize()) {
            timer.cancel();
            try {
                spawnLobby();
            } catch(IllegalStateException e){
                // probably do nothing (?)
                //TODO: check if this random assumption is actually true
            }
        }
    }

    private void startTimer(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(queue.size() > 1) {
                    spawnLobby();
                } else {
                    try {
                        // If there is only one player, restart the timer
                        startTimer();
                    } catch(Exception e){

                    }
                }
            }
        }, Configuration.getInstance().getQueueTimer());
    }

    private synchronized void spawnLobby(){
        System.out.println("Lobby spawned");
        List<String> elected = new ArrayList<>();
        while(!queue.isEmpty()){
            elected.add(queue.poll());
        }
        server.newLobby(elected);
    }

    private boolean queueMaxSize(){
        return queue.size() >= numberOfPlayers;
    }
}
