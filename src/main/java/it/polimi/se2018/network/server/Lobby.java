package it.polimi.se2018.network.server;

import it.polimi.se2018.controller.Controller;
import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;
import it.polimi.se2018.network.client.ClientMove;
import it.polimi.se2018.network.message.MatchStateMessage;
import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.message.MoveMessage;

import java.io.IOException;
import java.util.*;

public class Lobby implements Observer{
    private List<String> usernames;
    private Map<String, Player> playerMap;
    private Server server;
    private Controller controller;

    Lobby(List<String> usernames, Server server){
        this.usernames = usernames;
        newPlayerMap();
        this.server = server;
    }

    /**
     * Start match
     * This function creates a new controller, a new match and starts the controller.
     */
    private void startMatch() throws IOException {
        controller = new Controller(this);
    }

    /**
     * Initialize playerMap.
     * Create a Player object for every username in `usernames`
     */
    private void newPlayerMap(){
        playerMap = new HashMap<>();
        for(String username : usernames){
            playerMap.put(username, new Player(username));
        }
    }

    /**
     * Send a message (update clients' match state) to every lobby's client
     * @param message Message to be broadcasted
     */
    public void updateAll(Message message){
        for(String username : usernames){
            server.send(username, message);
        }
    }

    public List<Player> getPlayers(){
        List<Player> ret = new ArrayList<>();
        for (String username : usernames) {
            ret.add(playerMap.get(username));
        }
        return ret;
    }

    /**
     * Handle message.
     * This function is called when the server receives a message from one of the lobby's clients.
     * @param message Message to be handled
     */
    public void onReceive(Message message){
        switch(message.content){
            case TOOLCARD_REQUEST:
                //Controller -> Activate toolcard
                break;
            case PLAYER_MOVE:
                //Controller -> HandleMove
                PlayerMove move = convertMove(((MoveMessage) message).payload);
                controller.handleMove(move);
                break;
            default:
                // This should'n happen
                System.out.println("Received strange message");
        }
    }

    /**
     * Convert a ClientMove (created by client) to a PlayerMove (which contains references to objects in server)
     * @param move Move to convert
     * @return Converted move
     */
    private static PlayerMove convertMove(ClientMove move){
        return null;
    }

    @Override
    public void update(Observable match, Object o) {
        updateAll(new MatchStateMessage((Match) match));
    }
}
