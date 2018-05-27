package it.polimi.se2018.network.server;

import it.polimi.se2018.network.message.Message;
import it.polimi.se2018.network.client.ClientInterface;

import java.rmi.RemoteException;

public class Client {
    private String username;
    private ClientInterface clientInterface;
    private State state;

    public Client(String username, ClientInterface clientInterface) throws InvalidUsernameException {
        if(username.equals("admin")) throw new InvalidUsernameException();
        this.username = username;
        this.clientInterface = clientInterface;
        this.state = State.CONNECTED;
    }

    public void notify(Message message){
        try {
            clientInterface.notify(message);
        } catch (RemoteException e) {
            state = State.DISCONNECTED;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    enum State {
        CONNECTED, DISCONNECTED
    }
}