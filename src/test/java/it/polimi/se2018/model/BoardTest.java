package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class BoardTest {

    private Restriction pattern1[][];

    private Die red2;
    private Die blue4;
    private Die red4;
    private Die green1;

    @Before
    public void setUpTest() throws Exception {
        pattern1 = new Restriction[4][5];
        pattern1[0][0] = new Restriction(Color.RED);
        pattern1[1][2] = new Restriction(1);

        red2 = new Die(2, Color.RED);
        blue4 = new Die(4, Color.BLUE);
        red4 = new Die(4, Color.RED);
        green1 = new Die(1, Color.GREEN);
    }

    @Test
    public void dieAloneTest() throws Exception {
        Board board = new Board(pattern1, 1);

        PlacementError red200 = board.isDieAllowed(0, 0, red2);
        EnumSet<Flags> expected = EnumSet.of(Flags.NEIGHBOURS);
        assertTrue(red200.isEqual(expected));

        PlacementError blue421 = board.isDieAllowed(2, 1, blue4);
        expected = EnumSet.of(Flags.EDGE, Flags.VALUE, Flags.NEIGHBOURS);
        assertTrue(blue421.isEqual(expected));

        PlacementError green121 = board.isDieAllowed(2, 1, green1);
        expected = EnumSet.of(Flags.NEIGHBOURS, Flags.EDGE);
        assertTrue(green121.isEqual(expected));
    }

    @Test
    public void dieNearTest() throws Exception {
        Board board = new Board(pattern1, 1);

        board.setDie(0, 1, red4);
        PlacementError blue400 = board.isDieAllowed(0, 0, blue4);
        EnumSet<Flags> expected = EnumSet.of(Flags.VALUE, Flags.COLOR);
        assertTrue(blue400.isEqual(expected));
    }

    @Test
    public void cellNotEmptyTest() throws Exception {
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
    public void getCellTest() throws Exception {
        Board board = new Board(pattern1, 1);

        board.setDie(0, 0, red4);
        Cell c = board.getCell(0, 0);
        assertEquals(red4, c.getDie());

        try {
            board.getCell(-1, 2);
            fail();
        } catch(IndexOutOfBoundsException e){
            // do nothing
        }

        try {
            board.getCell(2, 4);
            fail();
        } catch(IndexOutOfBoundsException e){
            // do nothing
        }
    }

    @Test
    public void dieSetGetTest() throws Exception {
        Board board = new Board(pattern1, 1);

        board.setDie(2, 3, blue4);
        assertEquals(blue4, board.getDie(2, 3));

        try {
            board.setDie(-2, 3, red4);
            fail();
        } catch(IndexOutOfBoundsException e){
            // do nothing
        }

        try {
            board.getDie(5, 2);
            fail();
        } catch (IndexOutOfBoundsException e){
            // do nothing
        }

        board.setDie(2, 3, null);
        assertEquals(null, board.getDie(2, 3));
    }

    @Test
    public void columnRowsTest() throws Exception {
        Board board = new Board(pattern1, 1);
        board.setDie(0, 0, red4);
        board.setDie(0, 1, red2);
        board.setDie(0, 2, blue4);

        board.setDie(1, 1, blue4);
        board.setDie(1, 2, green1);

        board.setDie(3, 0, green1);

        List<Cell[]> columns = board.getColumns();
        List<Cell[]> rows = board.getRows();

        assertEquals(5, columns.size());
        assertEquals(4, rows.size());

        for(int i = 0; i < 5; i++){
            Cell[] col = new Cell[4];
            for(int j = 0; j < 4; j++){
                col[j] = board.getCell(i, j);
            }
            assertArrayEquals(col, columns.get(i));
        }

        for(int i = 0; i < 4; i++){
            Cell[] row = new Cell[5];
            for(int j = 0; j < 5; j++){
                row[j] = board.getCell(j, i);
            }
            assertArrayEquals(row, rows.get(i));
        }
    }

    @Test
    public void getBoardDifficultyTest() throws Exception {
        Board board = new Board(pattern1, 3);
        assertEquals(3, board.getBoardDifficulty());

        try {
            Board b = new Board(pattern1, -1);
            fail();
        } catch(InvalidParameterException e){
            // do nothing
        }
    }

    @Test
    public void countDicesTest() throws Exception {
        Board board = new Board(pattern1, 1);
        board.setDie(0, 0, red4);
        board.setDie(0, 1, red2);
        board.setDie(0, 2, blue4);
        board.setDie(1, 1, blue4);
        board.setDie(1, 2, green1);
        board.setDie(3, 0, green1);

        assertEquals(6, board.countDice());
    }

    @Test
    public void mementoPatternTest() throws Exception {
        Board board = new Board(pattern1, 1);
        board.setDie(0, 0, red4);
        board.setDie(0, 1, red2);
        board.setDie(0, 2, blue4);

        Board storedBoard = board.saveState();

        assertNotEquals(board, storedBoard);

        board.setDie(3, 0, green1);
        assertEquals(green1, board.getDie(3, 0));

        board.restoreState(storedBoard);
        assertEquals(null, board.getDie(3, 0));
    }
}