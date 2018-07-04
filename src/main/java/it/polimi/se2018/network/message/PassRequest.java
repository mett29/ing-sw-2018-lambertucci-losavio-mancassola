package it.polimi.se2018.network.message;

/**
 * A pass request message, to be sent to the server to request a "pass"
 */
public class PassRequest extends Message {
    public PassRequest(String username) {
        super(username, Content.PASS);
    }
}
