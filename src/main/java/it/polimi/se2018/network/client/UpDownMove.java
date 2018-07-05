package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

/**
 * Called in CLI when the player's state is UPDOWN (+1 and -1 are the possible actions)
 * @author mett29
 */
public class UpDownMove extends ClientMove {
    private final boolean isAdd;
    public UpDownMove(boolean isAdd) {
        super(ContentType.UPDOWN);
        this.isAdd = isAdd;
    }

    @Override
    public PlayerMove toPlayerMove(Player player, Match match) {
        return new PlayerMove<>(player, isAdd);
    }
}
