package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.PlayerMove;

public class PassMove extends ClientMove {
    public PassMove() {
        super(ContentType.PASS);
    }

    @Override
    public PlayerMove toPlayerMove(Player player, Match match) {
        //TODO
        return null;
    }
}