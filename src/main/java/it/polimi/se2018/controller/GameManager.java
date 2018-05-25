package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import it.polimi.se2018.utils.Extractor;

import java.util.List;

public class GameManager {
    private Match match;
    private RoundManager roundManager;
    private List<ToolCard> toolCardExtracted;
    private List<PublicObjCard> publicObjCardExtracted;
    private List<Player> firstPlayerQueue;

    GameManager() {
        this.toolCardExtracted = extractToolCards();
        this.publicObjCardExtracted = extractPublicObjCards();
        extractPrivateObjCard();

        this.match = new Match(firstPlayerQueue, (ToolCard[])(toolCardExtracted.toArray()), (PublicObjCard[])(publicObjCardExtracted.toArray()));
        //TODO: playerQueue

        this.roundManager = new RoundManager(match);
    }

    /**
     * Creates a deck of private objective cards. Sets a randomly extracted private objective card to all players.
     */
    private void extractPrivateObjCard() {
        Extractor<PrivateObjCard> privateObjCardDeck = new Extractor<>();

        for(Color color : Color.values())
            privateObjCardDeck.insert(new PrivateObjCard(color));

        for(Player player : match.getPlayers())
            player.setPrivateObjCard(privateObjCardDeck.extract());
    }

    /**
     * Creates a deck of toolcards. Returns a set of 3 cards.
     * @return 3 toolcards randomly extracted.
     */
    private List<ToolCard> extractToolCards() {
        Extractor<ToolCard> toolCardDeck = new Extractor<>();

        for(int n = 0; n < 12; n++)
            toolCardDeck.insert(new ToolCard(n));

        for(int i = 0; i < 3; i++)
            toolCardExtracted.add(toolCardDeck.extract());

        return toolCardExtracted;
    }

    /**
     * Creates a deck of public objective cards. Return a set of 3 cards.
     * @return 3 public objective cards randomly extracted.
     */
    private List<PublicObjCard> extractPublicObjCards() {
        Extractor<PublicObjCard> publicObjCardDeck = new Extractor<>();

        for(int n = 0; n < 10; n++)
            publicObjCardDeck.insert(new PublicObjCard(n));

        for(int i = 0; i < 3; i++)
            publicObjCardExtracted.add(publicObjCardDeck.extract());

        return publicObjCardExtracted;
    }

    /**
     * Calculates the final score of every player based on PrivateObjectiveCards, PublicObjectiveCards, tokens and empty cells.
     */
    void calculateScore() {
        for(Player player : match.getPlayers()) {
            int privCards, publCards = 0, tok, empty;

            privCards = player.getPrivateObjCard().getBonus(player.getBoard());

            for (PublicObjCard publicObjCard : publicObjCardExtracted)
                publCards += publicObjCard.getBonus(player.getBoard());

            tok = player.getToken();

            empty = 20 - player.getBoard().countDices();

            match.setScore(player, new Score(privCards, publCards, tok, empty));
        }
    }

    /**
     * Handle the player's move through RoundManager and checks if the match is finished.
     * @param move of the player
     * @return true if the match is finished, false otherwise
     */
    boolean handleMove(PlayerMove move) {
        if(roundManager.handleMove(move))
            roundManager.newRound(match);

        return match.getRoundTracker().getCurrentSize() == 10;
    }
}
