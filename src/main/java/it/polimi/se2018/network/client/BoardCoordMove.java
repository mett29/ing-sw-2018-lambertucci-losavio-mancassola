package it.polimi.se2018.network.client;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.model.PlayerMove;

public class BoardCoordMove extends ClientMove {
    public final int x;
    public final int y;
    public BoardCoordMove(int x, int y) {
        super(ContentType.BOARD_COORD);
        this.x = x;
        this.y = y;
    }


    @Override
    public PlayerMove toPlayerMove(Match match) {
        //TODO
        return null;
    }
}
