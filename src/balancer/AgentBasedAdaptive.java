package balancer;

import java.math.BigDecimal;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;

import client.Client;
import main.Log;
import server.Calculator;
import server.CalculatorImpl;
import server.Server;
import server.ServerCalculator;

/**
 * The class AgentBasedAdaptive is a Balancer which implements the Agent Based Adaptive way
 * 
 * @author Hannah Siegel
 * @version 2014-30-10
 *
 */
public class AgentBasedAdaptive implements Balancer{
	// All the registered Servers
	private HashMap<String, ServerCalculator> m_servers = new HashMap<String,ServerCalculator>();
	
	// The session information Client - Server
	private HashMap<String, String> m_session = new HashMap<String, String>();
	
	//Session Persistance 
	private boolean session_persistance = false;
	
	/**
	 * Constructor of the class, starts the Registry and binds the Balancer
	 * 
	 * @param name - name of the balancer 
	 * @param session_persistance - if Session Persistance should be used
	 */
	public AgentBasedAdaptive(String name,boolean session_persistance) {
		this.session_persistance = session_persistance;
		
		Balancer x;
		//Exporting stub
		try {
			x = (Balancer) UnicastRemoteObject.exportObject(this, 0);
		
			//creating the registry
			Registry registry = LocateRegistry.createRegistry(1099);
			
			//binding Balancer
			registry.bind(name, x);
		} catch (AlreadyBoundException e) {
			System.out.println("Load Balancer already bound, can not bound it again");
		} catch (RemoteException e) {
			System.out.println("There was a remote exception while exporting the Object: "+e.getMessage());
		}
		Log.logMin("Started Agent Based Adaptive LB ... ");
	}
	
	/**
	 * The method getServer returns the best Server
	 * This is the actual implementation of the algorithm
	 * 
	 * @param client_name
	 * @return ServerCalculator object
	 * @throws RemoteException
	 */
	private ServerCalculator getServer(String client_name) throws RemoteException{
		if ( m_servers.size() <= 0){
			Log.logMax("There is no server which could handle this request!");
			return null;
		}
		else{
			ServerCalculator choosen_server = null;
			int digits = 0;
			//if session persistance should be used, and the client has already a server as a friend, and this server is still availiable...
			if(session_persistance && m_session.containsKey(client_name) && m_servers.containsKey(m_session.get(client_name))){
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
				String servers_capacities = "";
				int smallest_load = 101;

				//find the server with the smallest load
				for (Entry entry : m_servers.entrySet()) {
					ServerCalculator server = (ServerCalculator) entry.getValue();
					int load = server.getCurrentWeight();
					if(load < smallest_load){
						smallest_load = load;
						servers_capacities =  entry.getKey().toString();
					}
				}
				
				// get the server object
				choosen_server =  m_servers.get(servers_capacities);
				
				//put the server information into the session persistance mapping
				if(!m_session.containsKey(client_name))
					m_session.put(client_name, choosen_server.getName());
				
				Log.logSession(client_name+" is using the service for the first time.",2);
				Log.logMax(client_name+"'s choosen server is "+choosen_server.getName());
			}
			
			// set the session information
			if(digits != 0)
				choosen_server.setDigits(client_name, digits);
			
			//return the Server
			return choosen_server;
		}
	}
		
	/* (non-Javadoc)
	 * @see server.Calculator#pi(server.Calculator.Type, java.lang.String)
	 */
	public BigDecimal pi(Type type,String c) throws RemoteException {
		Log.logMax("LB got an request from " + c);
		//get the server to handle the request
		ServerCalculator server_choosen = getServer(c);
		
		//return pi or null
		if (server_choosen != null)
			return server_choosen.pi(type, c);
		else 
			return null;
	}
	
	/* (non-Javadoc)
	 * @see balancer.Register#register(server.ServerCalculator, java.lang.String)
	 */
	public boolean register(ServerCalculator server,String server_name) throws RemoteException {
		m_servers.put(server_name,server);
		Log.logMax("Server " +server_name+ " registered at LoadBalancer. Current number of servers: "+m_servers.size() );
		return true;
	}

	/* (non-Javadoc)
	 * @see balancer.Register#unregister(server.ServerCalculator, java.lang.String)
	 */
	public boolean unregister(ServerCalculator s,String name) throws RemoteException {
		m_servers.remove(name,s);
		return true;
	}

	/* (non-Javadoc)
	 * @see server.Calculator#pi(int, server.Calculator.Type, java.lang.String)
	 */
	public BigDecimal pi(int digits,Type type,String c) throws RemoteException {
		Log.logMax("LB got the request ... ");
		
		//choose the server
		ServerCalculator server_choosen = getServer(c);
		
		//return pi
		if(server_choosen != null)
			return server_choosen.pi(digits,type,c);
		else
			return null;
	}
}
