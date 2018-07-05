package it.polimi.se2018.view;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.message.PatternRequest;
import it.polimi.se2018.network.message.UndoResponse;

public interface ViewInterface {
    /**
     * Display toolcard activation response
     * @param isOk False if the activation was rejected, true otherwise
     */
    void displayToolcardActivationResponse(boolean isOk);

    /**
     * Update match state in view. This will also handle the state change
     * @param match new match state
     */
    void updateMatch(Match match);

    /**
     * If connection was not rejected, make the view progress to the next phase
     * @param isOk False if connection was rejected
     */
    void onConnect(boolean isOk);

    /**
     * Display a connection error message
     * @param e Thrown exception
     */
    void displayConnectionError(Exception e);

    /**
     * Make the view progress to the "match phase".
     * This will initialize every component needed, based on the informations in Match.
     * This will also initialize the in-game timer visualization
     * @param match Initial match state
     * @param timerValue Initial timer value
     */
    void onMatchStart(Match match, int timerValue);

    /**
     * Display pool of board for the user to select from, and after the selection send a `PatternResponse` message to the Server
     * @param message Message containing the board pool and infos
     */
    void askPattern(PatternRequest message);

    /**
     * Display undo response
     * @param message Response message
     */
    void displayUndoMessage(UndoResponse message);

    /**
     * Reset in-game timer visualization
     */
    void resetTimer();
}