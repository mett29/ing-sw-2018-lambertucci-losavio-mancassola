package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.EnumSet;

import static org.junit.Assert.*;

public class PlayerTest {

    private Restriction restriction[][];
    private Board board1;
    private Board board2;

    @Before
    public void setUpTest() throws Exception {
        restriction = new Restriction[4][5];
        board1 = new Board(restriction, 1);
        board2 = new Board(restriction, 2);
    }

    @Test
    public void getNameTest() throws Exception {
        Player player;

        try {
            player = new Player(null);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    @Test
    public void getBoardTest() throws Exception {
        Player player = new Player("Test");
        player.setBoard(board1);

        assertEquals(board1, player.getBoard());

        assertNotEquals(board2, player.getBoard());
    }

    @Test
    public void getTokenTest() throws Exception {
        Player player = new Player("Test");
        player.setBoard(board1);
        player.setToken(1);

        assertEquals(1, player.getToken());

        assertNotEquals(2, player.getToken());
    }

    @Test
    public void tokenSetGetTest() throws Exception {
        Player player = new Player("Test");
        player.setBoard(board1);

        try {
            player.setToken(-1);
            fail();
        } catch (InvalidParameterException e) {
            //do nothing
        }
    }

    @Test
    public void privateCardSetGetTest() throws Exception {
        Player player = new Player("Test");

        try {
            player.setPrivateObjCard(null);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    @Test
    public void activatedToolcardSetGetTest() throws Exception {
        Player player = new Player("Test");

        try {
            player.setActivatedToolcard(null);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    @Test
    public void stateSetGetTest() throws Exception {
        Player player = new Player("Test");

        try {
            player.setState(null);
            fail();
        } catch (NullPointerException e) {
            //do nothing
        }
    }

    @Test
    public void possibleActionsTest() throws Exception {
        Player player = new Player("Test");

        player.possibleActionsSetUp();
        assertEquals(EnumSet.of(PossibleAction.ACTIVATE_TOOLCARD, PossibleAction.PICK_DIE, PossibleAction.PASS_TURN),player.getPossibleActions());

        player.possibleActionsRemove(PossibleAction.ACTIVATE_TOOLCARD);
        assertEquals(EnumSet.of(PossibleAction.PICK_DIE, PossibleAction.PASS_TURN),player.getPossibleActions());

        player.possibleActionsRemoveAll();
        assertEquals(EnumSet.noneOf(PossibleAction.class),player.getPossibleActions());
    }
}
