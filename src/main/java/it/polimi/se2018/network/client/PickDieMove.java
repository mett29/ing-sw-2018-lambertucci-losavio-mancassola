package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;

public class PickDieMove extends ClientMove {
    public PickDieMove() {
        super(ContentType.PICK_DIE);
    }

    @Override
    public PlayerMove toPlayerMove(Match match) {
        //TODO
        return null;
    }
}
