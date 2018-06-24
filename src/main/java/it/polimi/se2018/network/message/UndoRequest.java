package it.polimi.se2018.network.message;

public class UndoRequest extends Message {
    public UndoRequest(String username) {
        super(username, Content.UNDO_REQUEST);
    }
}
