package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class TurnManagerTest {
    private List<Player> players;
    private Player player1, player2;
    private Board board;
    private ToolCard[] toolCards;
    private PublicObjCard[] publicObjCards;
    private Match match;
    private Queue<Player> playerQueue;
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

        playerQueue = new LinkedList<>();
        playerQueue.add(player1);
        playerQueue.add(player2);
        playerQueue.add(player2);
        playerQueue.add(player1);

        observer = (obs, obj) -> { /*do nothing*/ };

        match = new Match(players, toolCards, publicObjCards, observer);

        match.setPlayerQueue(playerQueue);
    }

    @Test
    public void precheckFalse() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        playerQueue.peek().setToken(5);

        assertFalse(turnManager.activateToolcard(1));
    }

    @Test
    public void precheckTrue() throws Exception {
        TurnManager turnManager = new TurnManager(match);

        playerQueue.peek().getBoard().setDie(0, 0, new Die(2, Color.RED));
        playerQueue.peek().getBoard().setDie(0, 1, new Die(3, Color.RED));

        /*
         * (2R),(3R),(no),(no),(no)
         * (no),(no),(no),(no),(no)
         * (no),(no),(no),(no),(no)
         * (no),(no),(no),(no),(no)
         */

        playerQueue.peek().possibleActionsSetUp();
        playerQueue.peek().setToken(5);

        //Attivo la seconda toolcard -> Vado in stato PICK
        assertTrue(turnManager.activateToolcard(1));
        System.out.println(playerQueue.peek().getState().get());

        //Seleziono un dado dalla Board -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(playerQueue.peek(), new BoardCoord(playerQueue.peek().getBoard(), 0, 0), PossibleAction.ACTIVATE_TOOLCARD);

        assertFalse(turnManager.handleMove(pm2));
        System.out.println(playerQueue.peek().getState().get());

        //Seleziono una casella dalla Board -> TERMINATO -> Vado in stato IDLE
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(playerQueue.peek(), new BoardCoord(playerQueue.peek().getBoard(), 1, 1), PossibleAction.ACTIVATE_TOOLCARD);

        assertFalse(turnManager.handleMove(pm3));

        /*
         * (no),(3R),(no),(no),(no)
         * (no),(2R),(no),(no),(no)
         * (no),(no),(no),(no),(no)
         * (no),(no),(no),(no),(no)
         */

        System.out.println(playerQueue.peek().getState().get());

        System.out.println(playerQueue.peek().getBoard().getDie(1,1));
    }

    @Test
    public void passTurnTest() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        playerQueue.peek().setToken(5);

        PlayerMove pm = new PlayerMove<>(playerQueue.peek(), null, PossibleAction.PASS_TURN);
        pm.getActor().possibleActionsSetUp();

        assertTrue(turnManager.handleMove(pm));
        System.out.println(pm.getActor().getState().get());

        assertTrue(turnManager.handleMove(pm));
        System.out.println(pm.getActor().getState().get());
    }

    @Test
    public void sufficient_insufficientTokens() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        playerQueue.peek().setToken(3);

        assertTrue(turnManager.activateToolcard(0));

        assertTrue(turnManager.activateToolcard(0));

        assertEquals(0, playerQueue.peek().getToken());

        assertFalse(turnManager.activateToolcard(0));
    }
}
