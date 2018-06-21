package it.polimi.se2018.network.message;

public class PassRequest extends Message {
    public PassRequest(String username) {
        super(username, Content.PASS);
    }
}
