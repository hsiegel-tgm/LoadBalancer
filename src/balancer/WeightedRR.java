package balancer;

import java.math.BigDecimal;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.Client;
import main.Log;
import server.CalculatorImpl;
import server.Server;
import server.ServerCalculator;

/**
 * 
 * The class WeightedRR is a Balancer which implements the Weighted Round Robin way
 * 
 * @author Hannah Siegel
 * @version 2014-30-12
 */
public class WeightedRR implements Balancer {
	// All the registered Servers
	private HashMap<String, ServerCalculator> m_servers = new HashMap<String, ServerCalculator>();
	
	// The session information Client - Server
	private HashMap<String, String> m_session = new HashMap<String, String>();
	
	//The server weighted
	private String m_serverWeighted[];
	
	private int m_iterator;
	private boolean m_sessionPersistance = false;

	/**
	 * Constructor of the class, starts the Registry and binds the Balancer
	 * 
	 * @param balancer_name
	 * @param session_persistance
	 */
	public WeightedRR(String balancer_name, boolean session_persistance) {
		m_sessionPersistance = session_persistance;
		
		Balancer x;
		// Exporting stub
		try {
			x = (Balancer) UnicastRemoteObject.exportObject(this, 0);

			// creating the registry
			Registry registry = LocateRegistry.createRegistry(1099);

			// binding Balancer
			registry.bind(balancer_name, x);
		} catch (AlreadyBoundException e) {
			Log.error("Load Balancer already bound, can not bound it again",e);
		} catch (RemoteException e) {
			Log.error("There was a remote exception while exporting the Object",e);
		}
		
		Log.logMin("Started Weighted Round Robin LB ... ");
	}

	/**
	 *  The method allocate is generating the array for the algorithm
	 */
	private void allocate() {
		//all the servers
		HashMap<String, Integer> availiable_servers = new HashMap<String, Integer>();

		//fetching the information from the servers: <Servername, Weigth>
		for (Entry entry : m_servers.entrySet()) {
			ServerCalculator obj = (ServerCalculator) entry.getValue();
			try {
				availiable_servers.put(entry.getKey().toString(),obj.getWeight());
			} catch (RemoteException e) {
				Log.error("There was an remote exception when communication with one of the servers",e);
			}
		}

		//calculate total weigth
		int number = 0;
		for (Entry entry : availiable_servers.entrySet()) {
			Integer obj = (Integer) entry.getValue();
			number += obj;
		}

		Log.logAlg("Allocation has found out a total amount of " + number + " units");

		//generate servers array
		String servers[] = new String[number];
		int already_allocated = 0;
		while (already_allocated < number) {
			for (Entry entry : availiable_servers.entrySet()) {
				Integer capcity = (Integer) entry.getValue();
				if (capcity > 0) {
					String key = entry.getKey().toString();
					servers[already_allocated] = key;
					already_allocated++;
				}
			}
			
			HashMap<String, Integer> availiable_servers2 = new HashMap<String, Integer>();

			for (Entry entry : availiable_servers.entrySet()) {
				Integer capcity = (Integer) entry.getValue();
				availiable_servers2.put(entry.getKey().toString(), capcity - 1);
			}
			availiable_servers = availiable_servers2;
		}

		//log the allocation
		String s = "";
		for (int i = 0; i < number; i++) {
			s = s + servers[i] + " ";
		}
		Log.logAlg(s);

		availiable_servers.clear();

		//set the allocated values
		while (m_iterator != 0){}
		m_serverWeighted = servers;
	}

	/**
	 * The method getServer returns the best Server
	 * This is the actual implementation of the algorithm
	 * 
	 * @param client_name
	 * @return ServerCalculator object
	 * @throws RemoteException
	 */
	public ServerCalculator getServer(String client_name) throws RemoteException{
		if ( m_servers.size() <= 0){
			Log.logMax("There is no server which could handle this request!");
			return null;
		}
		else{
			ServerCalculator choosen_server = null;
			int digits = -1;
			
			//if session persistance should be used, and the client has already a server as a friend, and this server is still availiable...
			if(m_sessionPersistance && m_session.containsKey(client_name) && m_servers.containsKey(m_session.get(client_name))){
				//get the fried-server of the client (because of session persistance)
				choosen_server = m_servers.get(m_session.get(client_name));
				
				//Server to whom the client got dispatched
				Log.logSession(client_name+" has already a server to whom he was referred to: "+m_session.get(client_name),3);
				
				//if the server has to big of a load
				if(!(choosen_server.getCurrentWeight()<90)){
					Log.logSession(client_name+" has already a server to whom he was referred to: "+choosen_server.getName() +" but he is too busy.",2);
					m_session.remove(choosen_server.getName());
					digits = choosen_server.getDigits(client_name);
					Log.debug("Just got the clients digits:"+digits);
					choosen_server = null;
				}
			}
			if(choosen_server == null){
				
				//get the next server 'in line'
				try{
					choosen_server = (ServerCalculator) m_servers.get(m_serverWeighted[m_iterator]);
				}catch(ArrayIndexOutOfBoundsException e){
					choosen_server = (ServerCalculator) m_servers.get(m_serverWeighted[0]);
					m_iterator = 0;
				}
				
				m_iterator++;
				
				//reset iterartor	
				if(m_iterator==m_serverWeighted.length)
						m_iterator = 0;
				
				
				if(!m_session.containsKey(client_name)){
					m_session.put(client_name, choosen_server.getName());
				}
				
				Log.logSession(client_name+" is using the service for the first time. His choosen server is "+choosen_server.getName(),2);
			}
			if(digits != -1){
				Log.debug("lb is setting digits : "+client_name +" - "+ digits);
				choosen_server.setDigits(client_name, digits);
			}
			return choosen_server;
		}
	}

	/* (non-Javadoc)
	 * @see balancer.Register#register(server.ServerCalculator, java.lang.String)
	 */
	public boolean register(ServerCalculator server, String server_name) throws RemoteException {
		m_servers.put(server_name, server);
		
		Log.logMax("Server " + server_name
				+ " registered at LoadBalancer. Current number of servers: "
				+ m_servers.size());
		
		Log.logAlg("Calling allocation because " + server_name + " has registered...");
		
		allocate();
		return true;
	}

	/* (non-Javadoc)
	 * @see balancer.Register#unregister(server.ServerCalculator, java.lang.String)
	 */
	public boolean unregister(ServerCalculator s, String name)throws RemoteException {
		m_servers.remove(name, s);
		return true;
	}

	/* (non-Javadoc)
	 * @see server.Calculator#pi(server.Calculator.Type, java.lang.String)
	 */
	public BigDecimal pi(Type type, String client_name) throws RemoteException {
		Log.logMax("LB got an request from " + client_name);
		
		//get server
		ServerCalculator server_choosen = getServer(client_name);
		
		//return pi
		if (server_choosen != null)
			return server_choosen.pi(type, client_name);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see server.Calculator#pi(int, server.Calculator.Type, java.lang.String)
	 */
	public BigDecimal pi(int digits, Type type, String client_name)throws RemoteException {
		Log.logMax("LB got an request from " + client_name);
		
		//get Server
		ServerCalculator server_choosen = getServer(client_name);
		
		//return pi
		if (server_choosen != null)
			return server_choosen.pi(digits, type, client_name);
		else
			return null;
	}
}
