package main;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * The class Log is logging 
 * 
 * @author Hannah Siegel
 * @version 2014-30-12
 *
 */
public class Log {
	//different logging states
	private static boolean m_min = true;
	private static boolean m_normal = false;
	private static boolean m_max = false;
	private static boolean m_session = false;
	private static boolean m_algorithms = false;
	private static boolean m_results = false;

	public static void setIntensity(String s){
		if(s.equalsIgnoreCase("max")){
			m_normal=true;
			m_max = true;
		}else if(s.equalsIgnoreCase("normal")){
			m_normal=true;
		}
	}
	
	public static void setAlgorithmLogging(boolean b){
		m_algorithms = b;
	}
	
	public static void setSessionLogging(boolean b){
		m_session = b;
	}
	
	public static void setResultLogging(boolean b){
		m_results = b;
	}
	
	
    public static void logRes(String message) {
    	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
    	if(m_results)
    		System.out.println(a+" [RESULT]: "+message);
    }
    
    public static void logSession(String message,int prio) {
    	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
    	if(prio == 1 &&m_session)
    		System.out.println(a+" [SESSION]: "+message);	
    	if(prio == 2 && m_session && m_normal)
    		System.out.println(a+" [SESSION]: "+message);	
    	if(prio == 3 && m_session && m_max)
    		System.out.println(a+" [SESSION]: "+message);	
	}
    
    public static void logAlg(String message) {
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
    	if(m_algorithms)
    		System.out.println(a+" [ALGORITHM]: "+message);
     }
    
    public static void logMin(String message) {
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
    	if(m_min)
    		System.out.println(a+" [INFO]: "+message);
     }
    
    public static void log(String message) {
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
     	if(m_normal)
    		System.out.println(a+" [INFO]: "+message);
    }
    
    public static void logMax(String message) {
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
     	if(m_max)
    		System.out.println(a+" [INFO]: "+message);
    }
	
    /**
     * Logs a message with debug priority
     * @param message the message to be logged
     */
    public static void debug(String message) {
    	// String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
    	// System.out.println(a+" [DEBUG]: "+message);
    }

    /**
     * Logs a message with error priority
     * @param message the message to be logged
     */
    public static void error(String message,Exception e) {
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
     	System.out.println("\n ------------- \n "+ a+" [ERROR]: "+message +" - "+e.getMessage());
    }

    /**
     * Logs a message with warn priority
     * @param message the message to be logged
     */
    public static void warn(String message) {
     	String a = ( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) ).format( Calendar.getInstance().getTime() );
     	System.out.println( a+" [WARNING]: "+message);   
     }

	


}