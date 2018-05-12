package it.polimi.se2018.model;

import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.*;

public class DieTest {

    @Test
    public void dieCreationTest() throws Exception {
        Die die1;
        Die die2;

        try {
            die1 = new Die(-1, Color.RED);
            fail();
        } catch (InvalidParameterException e) {
            //do nothing
        }

        try {
            die2 = new Die(2, null);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    @Test
    public void dieDeepCopyTest() throws Exception {
        Die die1;
        Die die2 = null;

        try {
            die1 = new Die(die2);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }

        die2 = new Die(2, Color.RED);
        die1 = new Die(die2);

        assertTrue(die1.getValue() == die2.getValue());
        assertTrue(die1.getColor() == die2.getColor());
    }

    @Test
    public void dieGettersTest() throws Exception {
        Die die = new Die(2, Color.RED);

        assertEquals(2, die.getValue());
        assertNotEquals(1, die.getValue());

        assertEquals(Color.RED, die.getColor());
        assertNotEquals(Color.BLUE, die.getColor());
    }

    @Test
    public void dieRandomizeTest() throws Exception {
        Die die = new Die(2, Color.RED);

        die.randomize();

        if(die.getValue() < 1 || die.getValue() > 6 )
            fail();
         //do nothing
    }
}
