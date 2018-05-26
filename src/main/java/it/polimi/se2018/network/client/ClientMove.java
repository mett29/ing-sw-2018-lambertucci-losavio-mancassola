package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;

/**
 * Represent PlayerMove in client.
 * Since client can't reference any object in Model (i.e. the board), there is the need to have some kind of object representing a move in the client.
 * This object can be sent over the network and can be translated to PlayerMove in server via `toPlayerMove()`.
 */
public class ClientMove {
    //TODO

    /**
     * Translate ClientMove to a PlayerMove object, which helds references to `match` components
     * @param match
     * @return
     */
    public PlayerMove toPlayerMove(Match match){
        //TODO
        return null;
    }
}
