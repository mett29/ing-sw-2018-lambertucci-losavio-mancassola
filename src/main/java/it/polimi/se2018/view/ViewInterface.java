package it.polimi.se2018.view;

import it.polimi.se2018.model.Match;

public interface ViewInterface {
    void askLogin();
    void askTypeOfConnection();
    void onToolCardActivationResponse(boolean isOk);
    void updateMatch(Match match);
    void onConnect();
    void onConnectionError();
    void waitFor();
}
