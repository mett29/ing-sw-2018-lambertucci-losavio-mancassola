package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;

public class ValueMove extends ClientMove {
    public final int value;
    public ValueMove(int value) {
        super(ContentType.VALUE);
        this.value = value;
    }

    @Override
    public PlayerMove toPlayerMove(Match match) {
        //TODO
        return null;
    }
}
