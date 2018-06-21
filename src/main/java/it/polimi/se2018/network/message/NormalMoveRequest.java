package it.polimi.se2018.network.message;

public class NormalMoveRequest extends Message {
    public NormalMoveRequest(String username) {
        super(username, Content.NORMAL_MOVE);
    }
}
