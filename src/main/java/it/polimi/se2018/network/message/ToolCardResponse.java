package it.polimi.se2018.network.message;

public class ToolCardResponse extends Message {
    public final Type response;

    /**
     * Constructor
     * @param ok   False if Toolcard activation went wrong, true otherwise
     */
    public ToolCardResponse(boolean ok) {
        super("admin", Content.TOOLCARD_RESPONSE);
        if(ok){
            response = Type.OK;
        } else {
            response = Type.FAILURE;
        }
    }
}
