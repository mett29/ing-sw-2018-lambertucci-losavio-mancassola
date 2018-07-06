package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class DiceContainerCoordTest {

    private DiceContainer diceContainer;

    @Before
    public void setUpTest() throws Exception {
        diceContainer = new DiceContainer(5);
    }

    @Test
    public void diceContainerCoordCreation() throws Exception {
        DiceContainerCoord diceContainerCoord;

        try {
            diceContainerCoord = new DiceContainerCoord(null, 1);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }

        try {
            diceContainerCoord = new DiceContainerCoord(diceContainer, 6);
            fail();
        } catch (IndexOutOfBoundsException e) {
            //do nothing
        }
    }

    @Test
    public void diceContainerCoordSetGet() throws Exception {
        Die die = new Die(2, Color.RED);
        DiceContainerCoord diceContainerCoord = new DiceContainerCoord(diceContainer, 3);

        diceContainerCoord.set(null);

        diceContainerCoord.set(die);

        assertEquals(die, diceContainerCoord.get());
    }

    @Test
    public void dieAllowedTest() throws Exception {
        Die die1 = new Die(2, Color.RED);
        Die die2 = new Die(3, Color.BLUE);
        DiceContainerCoord diceContainerCoord = new DiceContainerCoord(diceContainer, 3);

        diceContainerCoord.set(die1);

        PlacementError pe = diceContainerCoord.isAllowed(die2);
        EnumSet<Flag> enum1 = EnumSet.of(Flag.NOTEMPTY);

        assertTrue(pe.isEqual(enum1));
    }

    @Test
    public void restoreStateTest() throws Exception {
        DiceContainerCoord diceContainerCoord = new DiceContainerCoord(diceContainer, 3);

        try {
            diceContainerCoord.restoreState();
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }
    }
}
