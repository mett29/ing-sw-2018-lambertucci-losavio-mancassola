package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class ActionTest {

    private Board b1, b2;
    private Die d1, d2;
    private Die sameColorD1, sameValueD2;
    private DieCoord c1, c2;

    @Before
    public void setUp() throws Exception {
        Restriction[][] pattern1 = new Restriction[4][5];
        Restriction[][] pattern2 = new Restriction[4][5];

        b1 = new Board(pattern1, 1);
        b2 = new Board(pattern2, 1);

        d1 = new Die(1, Color.GREEN);
        d2 = new Die(2, Color.BLUE);

        sameColorD1 = new Die(4, Color.GREEN);
        sameValueD2 = new Die(2, Color.RED);

        c1 = new BoardCoord(b1, 1, 2);
        c2 = new BoardCoord(b2, 4, 3);
    }

    /*
    @Test
    public void moveDiceTest() throws Exception{
        c1.set(d1);
        b2.setDie(3, 3, d1);

        Action a = new Switch(c1, c2);
        PlacementError errA = a.check();

        // There is a same color/value dice near c2;
        // c2 (end target cell) is empty;
        // Not near edges
        EnumSet<Flags> expectedA = EnumSet.of(Flags.COLOR, Flags.VALUE);
        assertTrue(errA.isEqual(expectedA));

        a.perform();

        assertEquals(null, c1.get());
        assertEquals(d1.getColor(), c2.get().getColor());
        assertEquals(d1.getValue(), c2.get().getValue());

    }
    */

    @Test
    public void switchDieTest() throws Exception{
        c1.set(d1);
        c2.set(d2);

        b1.setDie(1, 3, sameValueD2);
        b2.setDie(3, 3, sameColorD1);

        Action a = new Switch(c1, c2);
        PlacementError errA = a.check();

        // Near c2: same color of d1
        // Near c1: same value of d2
        // c2 (end target cell) is empty;
        // Not near edges
        EnumSet<Flags> expectedA = EnumSet.of(Flags.NOTEMPTY, Flags.COLOR, Flags.VALUE, Flags.EDGE);
        assertTrue(errA.isEqual(expectedA));
    }

    @Test
    public void setValueTest() throws Exception {
        c1.set(d1);
        Action a = new SetValue(c1, 4);
        assertTrue(a.check().isEqual(new PlacementError()));
        a.perform();
        assertEquals(4, c1.get().getValue());

        DieCoord c3 = new BoardCoord(b2, 0, 0); // empty cell
        a = new SetValue(c3, 4);
        try {
            a.perform();
            fail();
        } catch(NullPointerException e){
            // do nothing
        }
    }

    @Test
    public void flipTest() throws Exception {
        Action a = new Flip(c1);
        for(int i = 1; i < 7; i++){
            c1.set(new Die(i, Color.RED));
            a.perform();
            assertEquals(7 - i, c1.get().getValue());
        }
        c1.set(null);
        try {
            a.perform();
            fail();
        } catch(NullPointerException e){
            // do nothing
        }
    }

}