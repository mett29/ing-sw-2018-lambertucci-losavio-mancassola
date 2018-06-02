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
        playerQueue.peek().possibleActionsSetUp();
        playerQueue.peek().setState(new PlayerState(EnumState.YOUR_TURN));
        playerQueue.peek().setToken(5);

        assertFalse(turnManager.activateToolcard("Pino", 1));
    }

    @Test
    public void pickDieTest() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        Player currentPlayer = playerQueue.peek();
        currentPlayer.possibleActionsSetUp();
        currentPlayer.setState(new PlayerState(EnumState.YOUR_TURN));

        DiceContainer dc = new DiceContainer(5);
        dc.insert(new Die(2, Color.RED));

        assertTrue(turnManager.activateNormalMove("Pino"));

        PlayerMove<DieCoord> pm1 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(dc, 0));
        assertFalse(turnManager.handleMove(pm1));

        currentPlayer.getBoard().setDie(0, 0, new Die(1, Color.RED));
        currentPlayer.getBoard().setDie(0, 1, new Die(3, Color.YELLOW));

        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 0));
        assertFalse(turnManager.handleMove(pm2));

        pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 1));
        assertTrue(turnManager.handleMove(pm2));

        System.out.println(currentPlayer.getBoard().getDie(1,1));
        System.out.println(dc.getDie(0));
    }

    @Test
    public void pickFirstDie() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        Player currentPlayer = playerQueue.peek();
        currentPlayer.possibleActionsSetUp();
        currentPlayer.setState(new PlayerState(EnumState.YOUR_TURN));

        DiceContainer dc = new DiceContainer(5);
        dc.insert(new Die(2, Color.RED));
        dc.insert(new Die(3, Color.YELLOW));

        assertTrue(turnManager.activateNormalMove("Pino"));

        PlayerMove<DieCoord> pm1 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(dc, 0));
        assertFalse(turnManager.handleMove(pm1));

        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 1));
        assertTrue(turnManager.handleMove(pm2));

        System.out.println(currentPlayer.getBoard().getDie(0,1));
        System.out.println(dc.getDie(0));

        turnManager = new TurnManager(match);
        currentPlayer.possibleActionsSetUp();

        assertTrue(turnManager.activateNormalMove("Pino"));

        pm1 = new PlayerMove<>(currentPlayer, new DiceContainerCoord(dc, 1));
        assertFalse(turnManager.handleMove(pm1));

        pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 1));
        assertTrue(turnManager.handleMove(pm2));

        System.out.println(currentPlayer.getBoard().getDie(1,1));
        System.out.println(dc.getDie(1));
    }

    @Test
    public void precheckTrue_activationToolcardTest() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        Player currentPlayer = playerQueue.peek();
        currentPlayer.possibleActionsSetUp();
        currentPlayer.setToken(5);

        currentPlayer.getBoard().setDie(0, 0, new Die(2, Color.RED));
        currentPlayer.getBoard().setDie(0, 1, new Die(3, Color.RED));

        //Attivo la seconda toolcard -> Vado in stato PICK
        assertTrue(turnManager.activateToolcard("Pino", 1));
        System.out.println(playerQueue.peek().getState().get());

        //Seleziono un dado dalla Board -> Vado in stato PICK
        PlayerMove<DieCoord> pm2 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 0, 0));

        assertFalse(turnManager.handleMove(pm2));
        System.out.println(playerQueue.peek().getState().get());

        //Seleziono una casella dalla Board -> TERMINATO -> Vado in stato YOUR_TURN
        PlayerMove<DieCoord> pm3 = new PlayerMove<>(currentPlayer, new BoardCoord(currentPlayer.getBoard(), 1, 1));

        assertTrue(turnManager.handleMove(pm3));

        System.out.println(playerQueue.peek().getState().get());

        System.out.println(playerQueue.peek().getBoard().getDie(1,1));
    }

    @Test
    public void passTurnTest() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        Player currentPlayer = playerQueue.peek();
        currentPlayer.possibleActionsSetUp();
        currentPlayer.setState(new PlayerState(EnumState.YOUR_TURN));

        assertTrue(turnManager.passTurn("Pino"));

        assertFalse(turnManager.passTurn("Pino"));
        System.out.println(currentPlayer.getState().get());
    }

    @Test
    public void sufficient_insufficientTokens() throws Exception {
        TurnManager turnManager = new TurnManager(match);
        Player currentPlayer = playerQueue.peek();
        currentPlayer.possibleActionsSetUp();
        currentPlayer.setState(new PlayerState(EnumState.YOUR_TURN));
        currentPlayer.setToken(3);

        assertTrue(turnManager.activateToolcard("Pino", 0));

        assertTrue(turnManager.activateToolcard("Pino", 0));

        assertEquals(0, playerQueue.peek().getToken());

        assertFalse(turnManager.activateToolcard("Pino", 0));
    }
}
