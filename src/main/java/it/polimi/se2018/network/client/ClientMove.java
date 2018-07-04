package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

import java.io.Serializable;

/**
 * Represent PlayerMove in client.
 * Since client can't reference any object in Model (i.e. the board), there is the need to have some kind of object representing a move in the client.
 * This object can be sent over the network and can be translated to PlayerMove in server via `toPlayerMove()`.
 */
public abstract class ClientMove implements Serializable {
    public final ContentType contentType;
    protected ClientMove(ContentType contentType){
        this.contentType = contentType;
    }

    public enum ContentType {
        BOARD_COORD, DICE_CONTAINER_COORD, VALUE, UPDOWN, YESNO, PICK_DIE, PASS
    }

    /**
     * Translate ClientMove to a PlayerMove object, which helds references to `match` components
     *
     * @param player The actor of the move
     * @param match The match that is being played
     * @return A usable `PlayerMove.
     */
    public abstract PlayerMove toPlayerMove(Player player, Match match);
}
