package it.polimi.se2018;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Logger logger = Logger.getLogger("app");

        logger.log(Level.INFO, "Hello World!" );
    }
}
