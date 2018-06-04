package it.polimi.se2018.network.message;

import it.polimi.se2018.model.Board;
import it.polimi.se2018.network.server.ParsedBoard;

import java.util.List;

/**
 * This class encapsulates a board's pattern request
 */
public class PatternRequest extends Message {

    public final List<Board> boards;

    public PatternRequest(String username, List<Board> boards) {
        super(username, Content.PATTERN_REQUEST);
        this.boards = boards;
    }
}
