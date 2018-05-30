package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;

public class DiceContainerCoordMove extends ClientMove{
    public final int index;
    public final DiceContainerName container;
    public DiceContainerCoordMove(int index, DiceContainerName container) {
        super(ContentType.DICE_CONTAINER_COORD);
        this.container = container;
        this.index = index;
    }

    @Override
    public PlayerMove toPlayerMove(Match match) {
        //TODO
        return null;
    }

    public enum DiceContainerName {
        DRAFT_POOL, ROUND_TRACKER
    }
}
