package it.polimi.se2018.controller;

import java.io.*;
import java.util.Properties;

public class Configuration {
    private static Configuration ourInstance = new Configuration();
    public static Configuration getInstance() {
        return ourInstance;
    }

    private int socketPort;
    private int rmiPort;
    private int queueTimer;
    private int inGameTimer;

    /**
     * Load configuration from file. If file is not present, set default configuration
     */
    private Configuration() {
        // Read config file
        readConfig();
    }

    /**
     * This method reads the configuration parameters from 'config.properties' file
     */
    private void readConfig() {
        Properties prop = new Properties();
        String propertiesFileName = "config.properties";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName)) {
            if (inputStream != null) {
                prop.load(inputStream);

                socketPort = Integer.parseInt(prop.getProperty("socketPort", "1111"));
                rmiPort = Integer.parseInt(prop.getProperty("rmiPort", "1099"));
                queueTimer = Integer.parseInt(prop.getProperty("queueTimer", "30000"));
                inGameTimer = Integer.parseInt(prop.getProperty("inGameTimer", "20"));
            } else {
                //Set default configuration values
                socketPort = 1111;
                rmiPort = 1099;
                queueTimer = 30000;
                inGameTimer = 20;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public int getInGameTimer() {
        return inGameTimer;
    }
}
