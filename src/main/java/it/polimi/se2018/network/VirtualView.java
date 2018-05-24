package it.polimi.se2018.network;

import it.polimi.se2018.network.client.ClientInterface;

public class VirtualView {

    // TODO: Ancora da fare

    public VirtualView(ClientInterface client) {}

    public void update(String message) {
        System.out.println("Received " + message);
    }

    public void notify(String message) {
        notify(message);
    }
}
