package it.polimi.se2018.network.message;

public class UndoResponse extends Message {
    public final boolean ok;
    public UndoResponse(boolean b) {
        super("admin", Content.UNDO_RESPONSE);
        ok = b;
    }
}
