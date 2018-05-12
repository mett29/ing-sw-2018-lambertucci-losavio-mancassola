package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PublicObjCardTest {

    private int id;
    private Restriction pattern[][];
    private Die red1;
    private Die yellow2;
    private Die green3;
    private Die blue4;
    private Die purple5;
    private Die red2;
    private Die red3;
    private Die red4;
    private Die green4;
    private Die purple6;

    @Before
    public void setUpTest() throws Exception {
        id = 0;
        pattern = new Restriction[4][5];
        red1 = new Die(1, Color.RED);
        yellow2 = new Die(2, Color.YELLOW);
        green3 = new Die(3, Color.GREEN);
        blue4 = new Die(4, Color.BLUE);
        purple5 = new Die(5, Color.PURPLE);
        red2 = new Die(2, Color.RED);
        red3 = new Die(3, Color.RED);
        red4 = new Die(4, Color.RED);
        green4 = new Die(4, Color.GREEN);
        purple6 = new Die(6, Color.PURPLE);
    }

    @Test
    public void getTitleTest() throws NoSuchFieldException, IllegalAccessException {
        PublicObjCard publicObjCard = new PublicObjCard(id);
        assertEquals("Colori diversi - Riga", publicObjCard.getTitle());
    }

    @Test
    public void getDescriptionTest() throws NoSuchFieldException, IllegalAccessException {
        PublicObjCard publicObjCard = new PublicObjCard(id);
        assertEquals("Righe senza colori ripetuti", publicObjCard.getDescription());
    }

    @Test
    public void getBonusDifferentColorRowTest() throws NoSuchFieldException, IllegalAccessException {
        PublicObjCard publicObjCard1 = new PublicObjCard(0);
        PublicObjCard publicObjCard2 = new PublicObjCard(2);
        Board board = new Board(pattern, 1);

        assertEquals(0, publicObjCard1.getBonus(board));
        assertEquals(0, publicObjCard2.getBonus(board));

        board.setDie(0, 0, red1);
        board.setDie(1, 0, yellow2);
        board.setDie(2, 0, green3);
        board.setDie(3, 0, blue4);
        board.setDie(4, 0, purple5);

        assertEquals(6, publicObjCard1.getBonus(board));
        assertEquals(5, publicObjCard2.getBonus(board));
    }

    @Test
    public void getBonusDifferentColorColumnTest() throws NoSuchFieldException, IllegalAccessException {
        PublicObjCard publicObjCard1 = new PublicObjCard(1);
        PublicObjCard publicObjCard2 = new PublicObjCard(3);
        Board board = new Board(pattern, 1);

        assertEquals(0, publicObjCard1.getBonus(board));
        assertEquals(0, publicObjCard2.getBonus(board));

        board.setDie(0, 0, red1);
        board.setDie(0, 1, yellow2);
        board.setDie(0, 2, green3);
        board.setDie(0, 3, blue4);

        assertEquals(5, publicObjCard1.getBonus(board));
        assertEquals(4, publicObjCard2.getBonus(board));
    }

    @Test
    public void getBonusShadesSetTest() throws NoSuchFieldException, IllegalAccessException {
        PublicObjCard publicObjCard1 = new PublicObjCard(4);
        PublicObjCard publicObjCard2 = new PublicObjCard(5);
        PublicObjCard publicObjCard3 = new PublicObjCard(6);
        PublicObjCard publicObjCard4 = new PublicObjCard(7);
        Board board = new Board(pattern, 1);

        assertEquals(0, publicObjCard1.getBonus(board));
        assertEquals(0, publicObjCard2.getBonus(board));
        assertEquals(0, publicObjCard3.getBonus(board));
        assertEquals(0, publicObjCard4.getBonus(board));

        board.setDie(0, 0, red1);
        board.setDie(1, 0, green3);
        board.setDie(1, 1, purple5);

        assertEquals(0, publicObjCard1.getBonus(board));
        assertEquals(0, publicObjCard2.getBonus(board));
        assertEquals(0, publicObjCard3.getBonus(board));
        assertEquals(0, publicObjCard4.getBonus(board));

        board.setDie(1, 2, yellow2);
        board.setDie(2, 2, blue4);
        board.setDie(3, 2, purple6);

        assertEquals(2, publicObjCard1.getBonus(board));
        assertEquals(2, publicObjCard2.getBonus(board));
        assertEquals(2, publicObjCard3.getBonus(board));
        assertEquals(5, publicObjCard4.getBonus(board));
    }

    @Test
    public void getBonusColoredDiagonalTest() throws NoSuchFieldException, IllegalAccessException {
        PublicObjCard publicObjCard = new PublicObjCard(8);
        Board board = new Board(pattern, 1);

        assertEquals(0, publicObjCard.getBonus(board));

        board.setDie(0, 0, red1);
        board.setDie(1, 1, red2);

        assertEquals(2, publicObjCard.getBonus(board));

        board.setDie(3, 3, red3);

        assertEquals(2, publicObjCard.getBonus(board));

        board.setDie(2,2, red4);

        assertEquals(4, publicObjCard.getBonus(board));

        board.setDie(0, 1, green3);
        board.setDie(1, 0, green4);

        assertEquals(6, publicObjCard.getBonus(board));
    }

    @Test
    public void getVarietyOfColorTest() throws NoSuchFieldException, IllegalAccessException {
        PublicObjCard publicObjCard = new PublicObjCard(9);
        Board board = new Board(pattern, 1);

        assertEquals(0, publicObjCard.getBonus(board));

        board.setDie(0, 0, red1);
        board.setDie(1, 1, yellow2);
        board.setDie(1, 2, green3);
        board.setDie(2, 2, blue4);
        board.setDie(3, 2, purple5);

        assertEquals(4, publicObjCard.getBonus(board));
    }





}
