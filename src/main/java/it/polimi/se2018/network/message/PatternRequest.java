package it.polimi.se2018.network.message;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.model.PrivateObjCard;

import java.util.ArrayList;
import java.util.List;

/**
 * This class encapsulates a board's pattern request
 */
public class PatternRequest extends Message {

    public final ArrayList<Board> boards;
    public final ArrayList<String> boardNames;
    public final PrivateObjCard privateObjCard;

    public PatternRequest(String username, ArrayList<Board> boards, ArrayList<String> boardNames, PrivateObjCard privateObjCard) {
        super(username, Content.PATTERN_REQUEST);
        this.boards = boards;
        this.boardNames = boardNames;
        this.privateObjCard = privateObjCard;
    }
}
