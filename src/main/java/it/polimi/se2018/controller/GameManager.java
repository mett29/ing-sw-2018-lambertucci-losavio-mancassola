package it.polimi.se2018.controller;

import it.polimi.se2018.model.*;
import it.polimi.se2018.network.server.Lobby;
import it.polimi.se2018.utils.Extractor;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private Match match;
    private RoundManager roundManager;

    GameManager(Lobby lobby) {
        this.match = new Match(lobby.getPlayers(), extractToolCards(), extractPublicObjCards(), lobby);
        extractPrivateObjCard();
        match.notifyObservers();
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
    private static ToolCard[] extractToolCards() {
        Extractor<ToolCard> toolCardDeck = new Extractor<>();

        for(int n = 0; n < 12; n++)
            toolCardDeck.insert(new ToolCard(n));

        List<ToolCard> toolCardExtracted = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            toolCardExtracted.add(toolCardDeck.extract());

        return toolCardExtracted.toArray(new ToolCard[3]);
    }

    /**
     * Creates a deck of public objective cards. Return a set of 3 cards.
     * @return 3 public objective cards randomly extracted.
     */
    private static PublicObjCard[] extractPublicObjCards() {
        Extractor<PublicObjCard> publicObjCardDeck = new Extractor<>();

        for(int n = 0; n < 10; n++)
            publicObjCardDeck.insert(new PublicObjCard(n));

        List<PublicObjCard> publicObjCardExtracted = new ArrayList<>();
        for(int i = 0; i < 3; i++)
            publicObjCardExtracted.add(publicObjCardDeck.extract());

        return publicObjCardExtracted.toArray(new PublicObjCard[3]);
    }

    /**
     * Calculates the final score of every player based on PrivateObjectiveCards, PublicObjectiveCards, tokens and empty cells.
     */
    void calculateScore() {
        for(Player player : match.getPlayers()) {
            int privCards, publCards = 0, tok, empty;

            privCards = player.getPrivateObjCard().getBonus(player.getBoard());

            for (PublicObjCard publicObjCard : match.getPublicObjCards())
                publCards += publicObjCard.getBonus(player.getBoard());

            tok = player.getToken();

            empty = 20 - player.getBoard().countDices();

            match.setScore(player, new Score(privCards, publCards, tok, empty));

            match.notifyObservers();
        }
    }

    /**
     * Handle the player's move through RoundManager and checks if the match is finished.
     * @param move of the player
     * @return true if the match is finished, false otherwise
     */
    boolean handleMove(PlayerMove move) {
        boolean roundFinished = roundManager.handleMove(move);
        if(roundFinished) roundManager.newRound();
        if(match.getRoundTracker().getCurrentSize() == 10){
            return true;
        } else {
            match.notifyObservers();
            return false;
        }
    }
}
