package it.polimi.se2018.model;

import it.polimi.se2018.utils.Extractor;

import java.security.InvalidParameterException;
import java.util.*;

//The class that describes the match
public class Match {
    private List<Player> players;
    private ArrayList<Player> playerQueue;
    private Map<Player, Score> scores;
    private DiceContainer draftPool;
    private DiceContainer roundTracker;
    private List<ToolCard> toolCards;
    private List<PublicObjCard> publicObjCards;
    private Extractor<Die> diceBag;

    //constructor for `Match` class
    public Match(List<Player> players, ToolCard[] toolCards, PublicObjCard[] publicObjCards){
        if(players.size() < 2){
            throw new InvalidParameterException("players.size() must be at least 2");
        }
        this.players = players;

        if(toolCards.length != 3){
            throw new InvalidParameterException("toolCards.length must be 3");
        }
        if(publicObjCards.length != 3){
            throw new InvalidParameterException("publicObjCards.length must be 3");
        }

        this.toolCards = Arrays.asList(toolCards);
        this.publicObjCards = Arrays.asList(publicObjCards);

        this.roundTracker = new DiceContainer(10);
        diceBag = initDiceBag();

        draftPool = new DiceContainer(players.size() + 1);

        scores = new HashMap<>();
    }

    /**
     * Getter of a specific board of the match
     * @param player object to refer
     * @return the board of a player
     */
    public Board getBoard(Player player) {
        return player.getBoard();
    }

    /**
     * Getter of all players that are playing the match
     * @return the players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Getter of the player queue of the round
     * @return the player queue
     */
    public ArrayList<Player> getPlayerQueue() { return playerQueue; }

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
    public List<ToolCard> getToolCards() {
        return toolCards;
    }

    /**
     * Getter of all the public objective cards of the current match
     * @return all the public objective cards
     */
    public List<PublicObjCard> getPublicObjCards() {
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
        draftPool = newDraftPool;
    }

    /**
     * Extract a die from the dice bag
     * @return Extracted die (with randomized value)
     */
    public Die extractDie(){
        Die extractedDie = diceBag.extract();
        extractedDie.randomize();
        return extractedDie;
    }

    public void insertDie(Die die){
        diceBag.insert(die);
    }

    /**
     * Performs a specific action and returns any error
     * @param action to perform
     */
    public void performAction(Action action) {
        action.perform();
    }

    /**
     * Create a new dice bag
     * @return New dice bag initialized with 90 (18 * 5 colors) dice
     */
    private static Extractor<Die> initDiceBag() {
        Extractor<Die> ret = new Extractor<>();
        for(Color c : Color.values()){
            for(int i = 0; i < 18; i++){
                ret.insert(new Die(0, c));
            }
        }
        return ret;
    }
}
