package it.polimi.se2018.network.client;

import it.polimi.se2018.model.*;

public class DiceContainerCoordMove extends ClientMove{
    public final int index;
    public final DiceContainerName container;
    public DiceContainerCoordMove(int index, DiceContainerName container) {
        super(ContentType.DICE_CONTAINER_COORD);
        this.container = container;
        this.index = index;
    }

    @Override
    public PlayerMove toPlayerMove(Player player, Match match) {
        DiceContainer diceContainer;
        if (container == DiceContainerName.DRAFT_POOL) {
            diceContainer = match.getDraftPool();
        } else {
            diceContainer = match.getRoundTracker();
        }
        return new PlayerMove<>(player, new DiceContainerCoord(diceContainer, index));
    }

    public enum DiceContainerName {
        DRAFT_POOL, ROUND_TRACKER
    }
}
