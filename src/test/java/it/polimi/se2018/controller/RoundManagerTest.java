package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class RoundManagerTest {
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

        observer = (obs, obj) -> { /*do nothing*/ };

        match = new Match(players, toolCards, publicObjCards, observer);
    }

    @Test
    public void dequeue_and_newRoundTest() throws Exception {
        RoundManager roundManager = new RoundManager(match);

        //Sets last die of the container to null
        match.getDraftPool().setDie(4, null);

        //Shows actual draftpool
        System.out.println(match.getDraftPool().getDice());

        //Queue size is 4
        assertEquals(4, match.getPlayerQueue().size());

        //Pass the turn to the next player of the queue
        assertFalse(roundManager.passTurn("Pino"));

        //Queue size is 3
        assertEquals(3, match.getPlayerQueue().size());

        //Can't pass the turn because the current player is not "Pino"
        assertFalse(roundManager.passTurn("Pino"));

        //Queue size is still 3
        assertEquals(3, match.getPlayerQueue().size());

        //Pass the turn to the next player of the queue
        assertFalse(roundManager.passTurn("Gino"));

        //Queue size is 2
        assertEquals(2, match.getPlayerQueue().size());

        //Pass the turn to the next player of the queue
        assertFalse(roundManager.passTurn("Gino"));

        //Queue size is 1
        assertEquals(1, match.getPlayerQueue().size());

        //Pass the turn to the next player of the queue
        assertTrue(roundManager.passTurn("Pino"));

        //Queue size is 0
        assertEquals(0, match.getPlayerQueue().size());

        //Take the last die of the draftpool
        Die lastDie = match.getDraftPool().getLastDie();
        System.out.println(lastDie);

        //Check if the roundtracker current size is 0
        assertEquals(0, match.getRoundTracker().getCurrentSize());

        //Creates a new round
        roundManager.newRound();

        //Check if there is a new draftpool
        System.out.println(match.getDraftPool().getDice());

        //Check if the size of the playerQueue is again 4
        assertEquals(4, match.getPlayerQueue().size());

        //Check if the roundtracker is now 1
        System.out.println(match.getRoundTracker().getCurrentSize());

        //Check if the lastDie is the die inside the roundtracker (in first position)
        assertEquals(lastDie, match.getRoundTracker().getDie(0));
    }
}
