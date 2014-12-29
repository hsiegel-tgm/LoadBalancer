package main;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Log {

   // private static Logger logger = LogManager.getLogger();

    /**
     * Logs a message with debug priority
     * @param message the message to be logged
     */
    public static void debug(String message) {
       // logger.debug(message);
    	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
    	System.out.println(a+" [DEBUG]: "+message);
    	
    }

    /**
     * Logs a message with error priority
     * @param message the message to be logged
     */
    public static void error(String message) {
        // logger.debug(message);
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
     	System.out.println("\n ------------- \n "+ a+" [ERROR]: "+message);
    }

    /**
     * Logs a message with info priority
     * @param message the message to be logged
     */
    public static void info(String message) {
        // logger.debug(message);
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
     	System.out.println( a+" [INFO]: "+message);    }

    /**
     * Logs a message with warn priority
     * @param message the message to be logged
     */
    public static void warn(String message) {
        // logger.debug(message);
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
     	System.out.println( a+" [WARNING]: "+message);    }

}