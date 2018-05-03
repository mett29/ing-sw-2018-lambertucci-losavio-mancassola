package it.polimi.se2018.model;

//The score of the player
public class Score {
    private int privateObjCards;
    private int publicObjCards;
    private int tokens;
    private int emptyElems;

    public Score() {
        privateObjCards = 0;
        publicObjCards = 0;
        tokens = 0;
        emptyElems = 0;
    }

    public Score(int privCards, int publCards, int tok, int empty) {
        privateObjCards = privCards;
        publicObjCards = publCards;
        tokens = tok;
        emptyElems = empty;
    }

    /**
     * Getter of all the score values of the player
     * @return the 4 scores
     */
    public int[] getValues() {
        int[] ret = {privateObjCards, publicObjCards, tokens, emptyElems};

        return ret;
    }

    //sets all the 4 score values of the player

    /**
     * Setter of all the score values of the player
     * @param privCards score based on private objective cards
     * @param publCards score based on public objective cards
     * @param tok score based on remaining tokens
     * @param empty score based on empty cells of the player board
     */
    public void setValues(int privCards, int publCards, int tok, int empty) {
        privateObjCards = privCards;
        publicObjCards = publCards;
        tokens = tok;
        emptyElems = empty;
    }

    /**
     * Getter of the overall score of the player
     * @return the sum of the 4 scores
     */
    public int getOverallScore() {
        return publicObjCards + privateObjCards + tokens - emptyElems;
    }
}
