package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

/**
 * Used in CLI when the player makes a selection
 * @author MicheleLambertucci
 */
public class ValueMove extends ClientMove {
    public final int value;
    public ValueMove(int value) {
        super(ContentType.VALUE);
        this.value = value;
    }

    @Override
    public PlayerMove toPlayerMove(Player player, Match match) {
        return new PlayerMove<>(player, value);
    }
}
