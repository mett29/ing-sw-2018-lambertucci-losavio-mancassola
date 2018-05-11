package it.polimi.se2018.model;

import java.util.ArrayList;
import java.util.Map;

//The class that describes the match
public class Match {
    private Map<Player, Board> boards;
    private ArrayList<Player> players;
    private Map<Player, Score> scores;
    private DiceContainer draftPool;
    private DiceContainer roundTracker;
    private ArrayList<ToolCard> toolCards;
    private ArrayList<PublicObjCard> publicObjCards;
    private Extractor<Die> diceBag;

    //constructor for `Match` class
    public Match(int numOfPlayers){
        // TODO
    }

    /**
     * Getter of a specific board of the match
     * @param player object to refer
     * @return the board of a player
     */
    public Board getBoard(Player player) {
        return boards.get(player);
    }

    /**
     * Getter of all players that are playing the match
     * @return the players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * Getter of the round tracker of the match
     * @return the roundtracker
     */
    public DiceContainer getRoundTracker() {
        return roundTracker;
    }

    /**
     * Setter of the score of a player
     * @param player object to refer
     * @param score object to set
     */
    public void setScore(Player player, Score score) {
        scores.put(player, score);
    }

    /**
     * Getter of the score of a player
     * @param player object to refer
     * @return the score
     */
    public Score getScore(Player player) {
        return scores.get(player);
    }

    /**
     * Getter of all the toolcards of the current match
     * @return all the toolcards
     */
    public ArrayList<ToolCard> getToolCards() {
        return toolCards;
    }

    /**
     * Getter of all the public objective cards of the current match
     * @return all the public objective cards
     */
    public ArrayList<PublicObjCard> getPublicObjCards() {
        return publicObjCards;
    }

    /**
     * Getter of the draft pool of the turn (N.B.: not round, it's a different thing)
     * @return the current draftpool
     */
    public DiceContainer getDraftPool() {
        return draftPool;
    }

    /**
     * Setter of the draft pool for the next turn
     * @param newDraftPool object to set
     */
    public void setDraftPool(DiceContainer newDraftPool) {
        // TODO
    }

    /**
     * Getter of the dice bag of the match
     * @return the dicebag
     */
    public Extractor<Die> getDiceBag() {
        return diceBag;
    }

    /**
     * Performs a specific action and returns any error
     * @param action to perform
     * @return any error
     */
    public void performAction(Action action) {
        // TODO
        action.perform();
    }
}
