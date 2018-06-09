package it.polimi.se2018.controller;

public class Configuration {
    private static Configuration ourInstance = new Configuration();

    public static Configuration getInstance() {
        return ourInstance;
    }

    private String address;
    private int socketPort;
    private int rmiPort;
    private int queueTimer;

    /**
     * Load configuration from file. If file is not present, set default configuration
     */
    private Configuration() {
        //TODO: read config from file

        //Set default configuration values
        address = "localhost";
        socketPort = 1111;
        rmiPort = 1099;
        queueTimer = 30000;
    }

    public String getAddress() {
        return address;
    }

    public int getQueueTimer() {
        return queueTimer;
    }

    public int getRmiPort() {
        return rmiPort;
    }

    public int getSocketPort() {
        return socketPort;
    }
}
