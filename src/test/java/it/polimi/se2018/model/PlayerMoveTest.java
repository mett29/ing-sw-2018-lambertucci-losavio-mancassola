package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.*;

public class PlayerMoveTest {

    private Player player;

    @Before
    public void setUpTest() throws Exception {
        player = new Player("Test");
    }

    @Test
    public void assignActorTest() throws Exception {
        PlayerMove<Integer> playerMove1;

        try {
            playerMove1 = new PlayerMove<>(null, 0, PossibleAction.NO_ACTION);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }

        PlayerMove<Integer> playerMove2;

        playerMove2 = new PlayerMove<>(player, 0, PossibleAction.NO_ACTION);
    }

    @Test
    public void getMoveTest() throws Exception {
        Integer move = 1;
        PlayerMove<Integer> playerMove1 = new PlayerMove<>(player, 1, PossibleAction.NO_ACTION);
        PlayerMove<Integer> playerMove2 = new PlayerMove<>(player, 2, PossibleAction.NO_ACTION);

        assertEquals(move, playerMove1.getMove());

        assertNotEquals(move, playerMove2.getMove());
    }

    @Test
    public void stateGetSetTest() throws Exception {
        PlayerMove<Integer> playerMove = new PlayerMove<>(player, 1, PossibleAction.NO_ACTION);

        try {
            playerMove.setActorState(null);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }
    }
}
