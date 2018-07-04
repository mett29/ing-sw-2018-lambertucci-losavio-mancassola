package it.polimi.se2018.model;

import java.io.Serializable;
import java.security.InvalidParameterException;

/**
 * This class represents the player's score in the game
 * @author ontech7
 */
public class Score implements Serializable{
    private int privateObjCards;
    private int publicObjCards;
    private int tokens;
    private int emptyElements;

    public Score() {
        privateObjCards = 0;
        publicObjCards = 0;
        tokens = 0;
        emptyElements = 0;
    }

    public Score(int privCards, int publCards, int tok, int empty) {
        privateObjCards = privCards;
        publicObjCards = publCards;
        tokens = tok;
        emptyElements = empty;
    }

    /**
     * Getter of all the score values of the player
     * @return the 4 scores
     */
    public int[] getValues() {
        return new int[]{privateObjCards, publicObjCards, tokens, emptyElements};
    }

    /**
     * Setter of all the score values of the player
     * @param privCards score based on private objective cards
     * @param publCards score based on public objective cards
     * @param tok score based on remaining tokens
     * @param empty score based on empty cells of the player board
     */
    public void setValues(int privCards, int publCards, int tok, int empty) {
        if(privCards >= 0 && publCards >= 0 && tok >= 0 && empty >= 0) {
            privateObjCards = privCards;
            publicObjCards = publCards;
            tokens = tok;
            emptyElements = empty;
        } else {
            throw new InvalidParameterException("All sub-scores must be greater or equal than 0");
        }
    }

    /**
     * Getter of the overall score of the player
     * @return the sum of the 4 scores
     */
    public int getOverallScore() {
        return publicObjCards + privateObjCards + tokens - emptyElements;
    }
}
