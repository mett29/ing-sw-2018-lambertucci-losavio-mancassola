package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class BoardTest {

    private Restriction pattern1[][];

    private Die red2;
    private Die blue4;
    private Die red4;

    @Before
    public void setUp() throws Exception {
        pattern1 = new Restriction[4][5];
        pattern1[0][0] = new Restriction(Color.RED);
        pattern1[1][2] = new Restriction(1);

        red2 = new Die(2, Color.RED);
        blue4 = new Die(4, Color.BLUE);
        red4 = new Die(4, Color.RED);
    }

    @Test
    public void dieAlone() throws Exception {
        Board board = new Board(pattern1, 1);

        PlacementError red200 = board.isDieAllowed(0, 0, red2);
        EnumSet<Flags> expected = EnumSet.of(Flags.NEIGHBOURS);
        assertEquals(true, red200.isEqual(expected));

        PlacementError blue421 = board.isDieAllowed(2, 1, blue4);
        expected = EnumSet.of(Flags.EDGE, Flags.VALUE, Flags.NEIGHBOURS);
        assertTrue(blue421.isEqual(expected));
    }

    @Test
    public void dieNear() throws Exception {
        Board board = new Board(pattern1, 1);

        board.setDie(0, 1, red4);
        PlacementError blue400 = board.isDieAllowed(0, 0, blue4);
        EnumSet<Flags> expected = EnumSet.of(Flags.VALUE, Flags.COLOR);
        assertTrue(blue400.isEqual(expected));
    }

    @Test
    public void cellNotEmpty() throws Exception {
        Board board = new Board(pattern1, 1);

        board.setDie(0, 0, red4);
        PlacementError blue400 = board.isDieAllowed(0, 0, blue4);
        EnumSet<Flags> expected = EnumSet.of(Flags.NOTEMPTY, Flags.COLOR, Flags.NEIGHBOURS);
        assertTrue(blue400.isEqual(expected));
    }

    @Test
    public void getDie() throws Exception {
        Board board = new Board(pattern1, 1);

        board.setDie(2, 3, red4);
        assertEquals(red4, board.getDie(2, 3));
    }

    @Test
    public void getCell() throws Exception {
    }

    @Test
    public void setDie() throws Exception {
    }

    @Test
    public void getColumn() throws Exception {
    }

    @Test
    public void getRow() throws Exception {
    }

    @Test
    public void getColumns() throws Exception {
    }

    @Test
    public void getRows() throws Exception {
    }

    @Test
    public void getBoardDifficulty() throws Exception {
    }

    @Test
    public void countDices() throws Exception {
    }

    @Test
    public void iterator() throws Exception {
    }

    @Test
    public void saveState() throws Exception {
    }

    @Test
    public void restoreState() throws Exception {
    }

}