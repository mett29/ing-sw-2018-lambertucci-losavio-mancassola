package it.polimi.se2018.network.client;

import it.polimi.se2018.model.*;

/**
 * This class extends {@link ClientMove} to be used with BoardCoord
 * @author MicheleLambertucci
 */
public class BoardCoordMove extends ClientMove {
    private final int x;
    private final int y;
    public BoardCoordMove(int x, int y) {
        super(ContentType.BOARD_COORD);
        this.x = x;
        this.y = y;
    }


    @Override
    public PlayerMove toPlayerMove(Player player, Match match) {
        Board board = match.getBoard(player);
        return new PlayerMove<>(player, new BoardCoord(board, x, y));
    }
}
