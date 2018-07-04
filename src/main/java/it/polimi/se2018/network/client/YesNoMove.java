package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

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
