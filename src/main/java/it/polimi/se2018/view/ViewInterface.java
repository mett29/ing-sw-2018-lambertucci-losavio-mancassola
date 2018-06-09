package it.polimi.se2018.view;

import it.polimi.se2018.model.Match;
import it.polimi.se2018.network.message.PatternRequest;

public interface ViewInterface {
    void onToolCardActivationResponse(boolean isOk);
    void updateMatch(Match match);
    void onConnect();
    void onConnectionError(Exception e);
    void onMatchStart(Match match);
    void onPatternRequest(PatternRequest message);
}
