package it.polimi.se2018.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DiceContainerTest {

    @Test
    public void diceContainerCopyTest() throws Exception {
        DiceContainer diceContainer1 = null;
        DiceContainer diceContainer2;

        try {
            diceContainer2 = new DiceContainer(diceContainer1);
            fail();
        } catch (NullPointerException e){
            //do nothing
        }

        diceContainer1 = new DiceContainer(10);

        diceContainer1.insert(new Die(2, Color.BLUE));
        diceContainer1.insert(new Die(3, Color.RED));

        diceContainer2 = new DiceContainer(diceContainer1);

        assertEquals(diceContainer1.getCurrentSize(), diceContainer2.getCurrentSize());
        assertEquals(diceContainer1.getMaxSize(), diceContainer2.getMaxSize());
        for(int i = 0; i < diceContainer2.getCurrentSize(); i++) {
            assertEquals(diceContainer1.getDice().get(i).getValue(), diceContainer2.getDice().get(i).getValue());
            assertEquals(diceContainer1.getDice().get(i).getColor(), diceContainer2.getDice().get(i).getColor());
        }
        //TODO : Qua andrebbe Iterator per coprire quella parte di codice
    }

    @Test
    public void dieSetTest() throws Exception {
        DiceContainer diceContainer = new DiceContainer(5);
        Die die = new Die(2, Color.RED);

        try{
            diceContainer.setDie(7, die);
            fail();
        } catch (IndexOutOfBoundsException e) {
            //do nothing
        }

        assertTrue(diceContainer.isEmpty(4));
        diceContainer.setDie(4, die);
    }

    @Test
    public void saveRestoreStateTest() throws Exception{
        DiceContainer diceContainer = new DiceContainer(5);

        diceContainer.insert(new Die(2, Color.RED));
        diceContainer.insert(new Die(3, Color.BLUE));

        DiceContainer saveDiceContainer = diceContainer.saveState();

        for(int i = 0; i < saveDiceContainer.getCurrentSize(); i++) {
            assertEquals(diceContainer.getDice().get(i).getValue(), saveDiceContainer.getDice().get(i).getValue());
            assertEquals(diceContainer.getDice().get(i).getColor(), saveDiceContainer.getDice().get(i).getColor());
        }

        diceContainer.insert(new Die(4, Color.GREEN));

        assertEquals(diceContainer.getCurrentSize(), saveDiceContainer.getCurrentSize() + 1);

        diceContainer.restoreState(saveDiceContainer);

        assertEquals(diceContainer.getCurrentSize(), saveDiceContainer.getCurrentSize());
    }
}
