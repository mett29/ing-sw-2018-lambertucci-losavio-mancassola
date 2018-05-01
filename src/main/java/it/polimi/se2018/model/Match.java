package it.polimi.se2018.model;

import java.util.Map;

//The class that describes the match
public class Match {
    private Map<Player, Board> boards;
    private Player[] players;
    private Map<Player, Score> scores;
    private DiceContainer draftPool;
    private DiceContainer roundTracker;
    private ToolCard[] toolCards;
    private PublicObjCard[] publicObjCards;
    private Extractor<Die> diceBag;

    //constructor for `Match` class
    public Match(){}

    //gets the board of a specific player
    public Board getBoard(Player player) {

    }

    //gets all players that are playing the match
    public Player[] getPlayers() {

    }

    //gets the round tracker of the match
    public DiceContainer getRoundTracker() {

    }

    //sets the score of a player
    public void setScore(Player player, Score score) {

    }

    //gets the score of a player
    public Score getScore(Player player) {

    }

    //gets all the toolcards of the current match
    public ToolCard[] getToolCards() {

    }

    //gets all the toolcards of the current match
    public ObjCard[] getPublicObjCards() {

    }

    //gets the draft pool of the turn (N.B.: not round, it's a different thing)
    public DiceContainer getDraftPool() {

    }

    //sets the draft pool for the next turn
    public void setDraftPool(DiceContainer newDraftPool) {

    }

    //gets the dice bag of the match
    public Extractor<Die> getDiceBag() {

    }

    //performs a specific action and returns any error
    public PlacementError performAction(Action action) {

    }
}
