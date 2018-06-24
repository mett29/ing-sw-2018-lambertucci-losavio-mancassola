package it.polimi.se2018.network.message;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.network.server.ParsedBoard;

import java.util.List;

/**
 * This class encapsulates a board's pattern request
 */
public class PatternRequest extends Message {

    public final List<Board> boards;
    public final List<String> boardNames;

    public PatternRequest(String username, List<Board> boards, List<String> boardNames) {
        super(username, Content.PATTERN_REQUEST);
        this.boards = boards;
        this.boardNames = boardNames;
    }
}
