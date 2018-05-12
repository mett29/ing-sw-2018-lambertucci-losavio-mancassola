package it.polimi.se2018.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToolCardTest {

    private int id;

    @Before
    public void setUpTest() throws Exception{
        id = 0;
    }

    @Test
    public void getColorTest() throws NoSuchFieldException, IllegalAccessException {
        ToolCard toolCard = new ToolCard(id);
        assertEquals(Color.PURPLE, toolCard.getColor());
    }

    @Test
    public void getIdTest() throws NoSuchFieldException, IllegalAccessException {
        ToolCard toolCard = new ToolCard(id);
        assertEquals(id, toolCard.getId());
    }

    @Test
    public void getTitleTest() throws NoSuchFieldException, IllegalAccessException {
        ToolCard toolCard = new ToolCard(id);
        assertEquals("Pinza Sgrossatrice", toolCard.getTitle());
    }

    @Test
    public void getDescriptionTest() throws NoSuchFieldException, IllegalAccessException {
        ToolCard toolCard = new ToolCard(id);
        assertEquals("Dopo aver scelto un dado, aumenta o diminuisci il valore del dado scelto di 1. Non puoi cambiare un 6 in 1 o un 1 in 6.", toolCard.getDescription());
    }
}
