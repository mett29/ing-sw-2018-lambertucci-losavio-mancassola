package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import static org.junit.Assert.*;

public class TurnManagerTest {
    private List<Player> players;
    private Player player1, player2;
    private Board board;
    private ToolCard[] toolCards;
    private PublicObjCard[] publicObjCards;
    private PlayerMove<ToolCard> playerMove;
    private Match match;
    private Observer observer;

    @Before
    public void setUp() throws Exception {
        Restriction[][] pattern1 = new Restriction[4][5];

        board = new Board(pattern1, 3);

        player1 = new Player("Pino");
        player1.setBoard(board);
        player2 = new Player("Gino");

        players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        toolCards = new ToolCard[3];
        toolCards[0] = new ToolCard(0);
        toolCards[1] = new ToolCard(1);
        toolCards[2] = new ToolCard(2);

        publicObjCards = new PublicObjCard[3];
        publicObjCards[0] = new PublicObjCard(0);
        publicObjCards[1] = new PublicObjCard(1);
        publicObjCards[2] = new PublicObjCard(2);

        observer = (obs, obj) -> { /*do nothing*/ };

        match = new Match(players, toolCards, publicObjCards, observer);

        playerMove = new PlayerMove<>(player1, toolCards[1]);
    }

    @Test
    public void precheckFalse() throws Exception {
        TurnManager turnManager = new TurnManager(match);

        assertFalse(turnManager.handleMove(playerMove));
    }
}
