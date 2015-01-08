package server;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import main.Log;
import balancer.Balancer;
import balancer.Register;
import client.Client;

/**
 * The class Server is the implementation an an server which calculates pi
 * 
 * @author Hannah Siegel
 * @version 2014-12-30
 *
 */
public class Server implements ServerCalculator{
	// session persistance information
	private HashMap<String, Integer> m_session_clients = new HashMap<String,Integer>();
	// cpu number
	private static int m_num_cpu = Runtime.getRuntime().availableProcessors();
	//weight of the server
	private final int m_weight;
	// balancer obj
	private Balancer m_balancer;
	//servers's name
	private String m_servername;
	//clac object
	private CalculatorImpl m_calc;
	
	/**
	 * Adds new Server, Server connects to LB, adds the server to the LB
	 * 
	 * @param loadbalancerIP
	 * @param loadbalancerName
	 * @param weight
	 * @param servername
	 */
	public Server(String loadbalancerIP, String loadbalancerName, int weight, String servername ) {
		// setting global variables
		m_servername = servername;
		m_weight = weight;
		
		// calculator object
		m_calc = new CalculatorImpl();
		
		try{
			//fetch registry
			Registry registry = LocateRegistry.getRegistry(loadbalancerIP,1099);
			
			//fetch balancer
			m_balancer = (Balancer) registry.lookup(loadbalancerName);
		} catch (RemoteException e) {
			Log.error("There was a remote error, could not find registry on "+loadbalancerIP,e);
		} catch (NotBoundException e) {
			Log.error("Server not bound: "+loadbalancerName,e);
			System.exit(-1);
		}
		
		ServerCalculator x;
		//bind server
		try {
			x = (ServerCalculator) UnicastRemoteObject.exportObject(this, 0);
			
			m_balancer.register(x, servername);

		} catch (RemoteException e) {
			Log.error("There was an remote exception.",e);
		}
		
		Log.log("Started " +m_servername+ " with the weight "+m_weight+ " ... ");
	}

	
	
	
	//TODO get current bla
	/* (non-Javadoc)
	 * @see server.ServerCalculator#getCurrentWeight()
	 */
	public int getCurrentWeight() {
		// Log.debug(m_servername+": LOAD: "+getCurrentSystemLoad());
		int ram = getCurrentRAMLoad();
		Log.debug("ram: "+ram);

		    
		int weight = (int)(Math.random()*100)+1;
		Log.logAlg(m_servername + "has the load: "+weight);
		return weight; //TODO
	}
	
	/**
	 * @return
	 */
	public static double getCurrentSystemLoad(){
		double res = Double.NaN;
		OperatingSystemMXBean mx;
		try{
			 mx = ManagementFactory.getOperatingSystemMXBean();
			 res = mx.getSystemLoadAverage();
		}catch(Throwable t){
		}
		Log.debug(res+" - CURRENT SYS LOAD");
		return res;
	}
	
	/**
	 * @return
	 */
	public static int getCurrentRAMLoad(){
		int res = 0;
		Runtime runtime = Runtime.getRuntime();
		long ram_availiabe=runtime.totalMemory();
		long ram_used = runtime.totalMemory() - runtime.freeMemory();
		
		res  = (int)( ((100.0 / (double)ram_availiabe)*(double)ram_used)+0.5);
		
		return res;
	}	

	/* (non-Javadoc)
	 * @see server.Calculator#pi(server.Calculator.Type, java.lang.String)
	 */
	public BigDecimal pi(Type type,String c) throws RemoteException {
		Log.logAlg(m_servername+" just got a request from "+c);
		
		// if the server know how many digits the client wants (session persistance)
		if(m_session_clients.containsKey(c)){
			int digits = m_session_clients.get(c);
			
			if (digits < 0)
				digits = 10;
			
			Log.logSession(m_servername+" got an request from "+c+" . He didn't specify the digits, so he took "+digits+ " digits",1);
			
			return m_calc.pi(digits,type,c);
		}			
		
		Log.logSession(m_servername+" got an request from "+c+" . It was his first request and therefore he couldnt specify any digits.",2);
		return m_calc.pi(type,c);
	}

	public BigDecimal pi(int digits, Type type,String c) throws RemoteException {
		Log.logAlg(m_servername+" just got the request from "+c);
		
		//adding the session information
		m_session_clients.put(c, digits);

		//int new_digits;
		//if(m_session_clients.containsKey(c)){
		//	m_session_clients.put(c, digits);
		//}else{
		//	m_session_clients.put(c, digits);
		//	new_digits = digits;
		//}
		Log.logSession(m_servername+" just added an session information about "+c+": He wanted "+digits +" digits.",2);
		
		return m_calc.pi(digits,type,c);
	}
	
	/* (non-Javadoc)
	 * @see server.ServerCalculator#getName()
	 */
	public String getName(){
		return m_servername;
	}

	/* (non-Javadoc)
	 * @see server.ServerCalculator#getDigits(java.lang.String)
	 */
	public int getDigits(String client) throws RemoteException {
		return m_session_clients.get(client);
	}

	/* (non-Javadoc)
	 * @see server.ServerCalculator#setDigits(java.lang.String, int)
	 */
	public void setDigits(String client, int digits) throws RemoteException {
		Log.debug("set dig "+m_servername+" "+client+" "+digits);
		m_session_clients.put(client, digits);
	}
	
	/* (non-Javadoc)
	 * @see server.ServerCalculator#getWeight()
	 */
	public int getWeight() {
		return m_weight;
	}
	
}
