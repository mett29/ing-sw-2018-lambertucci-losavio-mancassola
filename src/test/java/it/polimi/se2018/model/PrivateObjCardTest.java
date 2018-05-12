package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PrivateObjCardTest {

    private Color cardColor;
    private Restriction pattern[][];
    private Die blue2;
    private Die green3;

    @Before
    public void setUpTest() throws Exception {
        cardColor = Color.BLUE;
        pattern = new Restriction[4][5];
        blue2 = new Die(2, Color.BLUE);
        green3 = new Die(3, Color.GREEN);
    }

    @Test
    public void getColorTest() throws NoSuchFieldException, IllegalAccessException {
        PrivateObjCard privateObjCard = new PrivateObjCard(cardColor);
        assertEquals(cardColor, privateObjCard.getColor());
    }

    @Test
    public void getTitleTest() throws NoSuchFieldException, IllegalAccessException {
        PrivateObjCard privateObjCard = new PrivateObjCard(cardColor);
        assertEquals("Sfumature Blu", privateObjCard.getTitle());
    }

    @Test
    public void getDescriptionTest() throws NoSuchFieldException, IllegalAccessException {
        PrivateObjCard privateObjCard = new PrivateObjCard(cardColor);
        assertEquals("Somma dei valori su tutti i dadi Blu", privateObjCard.getDescription());
    }

    @Test
    public void getBonusTest() throws NoSuchFieldException, IllegalAccessException {

        PrivateObjCard privateObjCard = new PrivateObjCard(cardColor);
        Board board = new Board(pattern, 1);

        board.setDie(0,0, green3);

        assertEquals(0, privateObjCard.getBonus(board));

        board.setDie(1,2,blue2);

        assertEquals(2, privateObjCard.getBonus(board));
    }
}
