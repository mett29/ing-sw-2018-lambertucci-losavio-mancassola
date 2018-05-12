package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.*;

public class DieCoordTest {
    private Restriction pattern1[][];

    private Die red2, blue4, green4, purple1;


    @Before
    public void setUp() throws Exception {
        pattern1 = new Restriction[4][5];
        pattern1[0][0] = new Restriction(Color.RED);
        pattern1[1][2] = new Restriction(1);
        pattern1[3][2] = new Restriction(Color.GREEN);

        red2 = new Die(2, Color.RED);
        blue4 = new Die(4, Color.BLUE);
        green4 = new Die(4, Color.GREEN);
        purple1 = new Die(1, Color.PURPLE);
    }

    @Test
    public void creation() throws Exception {
        Board board = new Board(pattern1, 2);
        DieCoord a = new BoardCoord(board, 2, 3);
        try {
            DieCoord wrong = new BoardCoord(board, -2, 4);
            fail();
        } catch(IndexOutOfBoundsException e){
            // do nothing
        }

        try {
            DieCoord wrong = new BoardCoord(null, 1, 2);
            fail();
        } catch (NullPointerException e){
            // do nothing
        }

        DiceContainer diceContainer = new DiceContainer(5);
        DieCoord b = new DiceContainerCoord(diceContainer, 2);
        try {
            DieCoord wrong = new DiceContainerCoord(diceContainer, 6);
            fail();
        } catch(IndexOutOfBoundsException e){
            // do nothing
        }

        try {
            DieCoord wrong = new DiceContainerCoord(null, 1);
            fail();
        } catch (NullPointerException e){
            // do nothing
        }
    }

    @Test
    public void setGetTestNotNull() throws Exception {
        Board board = new Board(pattern1, 2);
        DieCoord a = new BoardCoord(board, 2, 3);
        a.set(green4);
        assertEquals(green4, board.getDie(2, 3));
        assertEquals(board.getDie(2, 3), a.get());

        DiceContainer diceContainer = new DiceContainer(4);
        DieCoord b = new DiceContainerCoord(diceContainer, 2);
        b.set(green4);
        assertEquals(green4, board.getDie(2, 3));
        assertEquals(board.getDie(2, 3), b.get());
    }

    @Test
    public void setGetTestNull() throws Exception {
        Board board = new Board(pattern1, 2);
        DieCoord a = new BoardCoord(board, 2, 3);
        assertEquals(null, a.get());

        DiceContainer diceContainer = new DiceContainer(4);
        DieCoord b = new DiceContainerCoord(diceContainer, 2);
        assertEquals(null, b.get());
    }

    @Test
    public void mementoTest() throws Exception {
        Board board = new Board(pattern1, 2);
        DieCoord a = new BoardCoord(board, 2, 3);

        a.saveState();
        a.set(green4);
        assertEquals(green4, a.get());
        a.restoreState();
        assertEquals(null, a.get());

        try {
            a.restoreState();
            fail();
        } catch(NullPointerException e){
            // do nothing
        }

        DiceContainer diceContainer = new DiceContainer(4);
        DieCoord b = new DiceContainerCoord(diceContainer, 2);

        b.saveState();
        b.set(green4);
        assertEquals(green4, b.get());
        b.restoreState();
        assertEquals(null, b.get());

        try {
            b.restoreState();
            fail();
        } catch(NullPointerException e){
            // do nothing
        }
    }

    @Test
    public void isAllowedTest() throws Exception {
        Board board = new Board(pattern1, 2);
        DieCoord a = new BoardCoord(board, 0, 0);

        board.setDie(0, 1, red2);
        assertTrue(a.isAllowed(green4).isEqual(board.isDieAllowed(0, 0, green4)));

        DiceContainer diceContainer = new DiceContainer(4);
        DieCoord b = new DiceContainerCoord(diceContainer, 2);
        assertTrue(b.isAllowed(green4).isEqual(new PlacementError()));

        b.set(green4);
        assertTrue(b.isAllowed(blue4).isNotEmptyError());
    }
}