package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

/**
 * Used in CLI when the player's state is YESNO and the possible actions are: YES & NO
 * @author MicheleLambertucci
 */
public class YesNoMove extends ClientMove {
    private final boolean isYes;
    public YesNoMove(boolean isYes) {
        super(ContentType.UPDOWN);
        this.isYes = isYes;
    }

    @Override
    public PlayerMove toPlayerMove(Player player, Match match) {
        return new PlayerMove<>(player, isYes);
    }
}
