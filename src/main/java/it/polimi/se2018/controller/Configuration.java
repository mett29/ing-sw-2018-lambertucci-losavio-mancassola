package it.polimi.se2018.controller;

import java.io.*;
import java.util.Properties;

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

                address = prop.getProperty("address");
                socketPort = Integer.parseInt(prop.getProperty("socketPort"));
                rmiPort = Integer.parseInt(prop.getProperty("rmiPort"));
                queueTimer = Integer.parseInt(prop.getProperty("queueTimer"));
            } else {
                //Set default configuration values
                address = "localhost";
                socketPort = 1111;
                rmiPort = 1099;
                queueTimer = 30;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
