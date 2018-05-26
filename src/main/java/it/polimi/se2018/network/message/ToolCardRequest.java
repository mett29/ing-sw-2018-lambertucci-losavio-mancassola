package it.polimi.se2018.network.message;

/**
 * This class encapsulates a ToolCard activation request
 */
public class ToolCardRequest extends Message{
    public final int index;

    /**
     * Constructor
     *
     * @param username Who created the message
     * @param index    Index of the toolcard you want to activate
     */
    public ToolCardRequest(String username, int index) {
        super(username, Content.TOOLCARD_REQUEST);
        this.index = index;
    }
}
