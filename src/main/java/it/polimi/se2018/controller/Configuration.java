package it.polimi.se2018.controller;

import java.io.*;
import java.util.Properties;

/**
 * This class represents the server configuration, loaded from file 'config.properties'
 * @author MicheleLambertucci, mett29
 */
public class Configuration {
    private static Configuration ourInstance = new Configuration();
    public static Configuration getInstance() {
        return ourInstance;
    }

    private int socketPort;
    private int rmiPort;
    private int queueTimer;
    private int inGameTimer;
    private String patternPath;

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
        String dir = System.getProperty("user.dir");
        try (InputStream inputStream = new FileInputStream(dir + "/" + propertiesFileName)) {
            prop.load(inputStream);

            socketPort = Integer.parseInt(prop.getProperty("socketPort", "1111"));
            rmiPort = Integer.parseInt(prop.getProperty("rmiPort", "1099"));
            queueTimer = Integer.parseInt(prop.getProperty("queueTimer", "30000"));
            inGameTimer = Integer.parseInt(prop.getProperty("inGameTimer", "20"));
            patternPath = prop.getProperty("patternPath", "null");
        } catch (Exception e) {
            socketPort = 1111;
            rmiPort = 1099;
            queueTimer = 30000;
            inGameTimer = 20;
            patternPath = "null";
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

    public String getPatternPath() { return patternPath; }
}
