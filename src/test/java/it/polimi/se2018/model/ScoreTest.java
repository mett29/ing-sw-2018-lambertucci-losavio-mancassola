package it.polimi.se2018.model;

import org.junit.Test;

import java.security.InvalidParameterException;

import static org.junit.Assert.*;

public class ScoreTest {

    @Test
    public void getScoreTest() throws Exception {
        Score score = new Score(3, 4, 1, 1);

        assertArrayEquals(new int[]{3, 4, 1, 1},score.getValues());
    }

    @Test
    public void scoreSetGetTest() throws Exception {
        Score score = new Score();

        try {
            score.setValues(1, -2, 3, 1);
            fail();
        } catch (InvalidParameterException e) {
            //do nothing
        }
    }

    @Test
    public void getOverallScoreTest() throws Exception {
        Score score = new Score(3, 4, 1, 2);

        assertEquals(6, score.getOverallScore());
    }
}
