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

public class AgentBasedAdaptive implements Balancer{
	private HashMap<String, ServerCalculator> m_servers = new HashMap<String,ServerCalculator>();
	private HashMap<String, String> m_session = new HashMap<String, String>();
	private boolean session_persistance = false;
	
	public AgentBasedAdaptive(String name,boolean sp) {
		session_persistance = sp;
		Balancer x;
		//Exporting stub
		try {
			x = (Balancer) UnicastRemoteObject.exportObject(this, 0);
		
			//getting the registry
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
	
	private int getNumbers(){
		int num = 0;
		for (Entry entry : m_servers.entrySet()) {
			 ServerCalculator obj = (ServerCalculator) entry.getValue();
			 try {
				num += obj.getWeight();
			} catch (RemoteException e) {
				Log.error("There was an remote exception when communication with one of the servers",e);
			}
		}
		return num;
	}
	
	private ServerCalculator getServer(String c) throws RemoteException{
		
		if ( m_servers.size() <= 0){
			Log.logMax("There is no server which could handle this request!");
			return null;
		}
		else{
			ServerCalculator choosen_server = null;
			//if session persistance should be used, and the client has already a server as a friend, and this server is still availiable...
			if(session_persistance && m_session.containsKey(c) && m_servers.containsKey(m_session.get(c))){
				choosen_server = m_servers.get(m_session.get(c));
				Log.logSession(c+" has already a server to whom he was referred to: "+m_session.get(c),3);
				if(!(choosen_server.getCurrentWeight()<80)){
					Log.logSession(c+" has already a server to whom he was referred to: "+choosen_server.getName() +" but he is too busy.",2);
					m_session.remove(choosen_server.getName());
					choosen_server = null;
				}
			}
			if(choosen_server == null){
				String servers_capacities = "";
				int smallest_load = 101;

				for (Entry entry : m_servers.entrySet()) {
					ServerCalculator server = (ServerCalculator) entry.getValue();
					int load = server.getCurrentWeight();
					if(load < smallest_load){
						smallest_load = load;
						servers_capacities =  entry.getKey().toString();
					}
				}
				
				choosen_server =  m_servers.get(servers_capacities);
				
				if(!m_session.containsKey(c)){
					m_session.put(c, choosen_server.getName());
				}
				
				Log.logSession(c+" is using the service for the first time. His choosen server is "+choosen_server.getName(),2);

			}
			return choosen_server;
		}
	}
		
	
	public BigDecimal pi(Type type,String c) throws RemoteException {
		Log.logMax("LB got an request from " + c);
		ServerCalculator server_choosen = getServer(c);
		if (server_choosen != null)
			return server_choosen.pi(type, c);
		else
			return null;
	}
	
	public boolean register(ServerCalculator s,String name) throws RemoteException {
		m_servers.put(name,s);
		Log.logMax("Server " +name+ " registered at LoadBalancer. Current number of servers: "+m_servers.size() );
		return true;
	}

	public boolean unregister(ServerCalculator s,String name) throws RemoteException {
		m_servers.remove(name,s);
		return true;
	}

	public BigDecimal pi(int digits,Type type,String c) throws RemoteException {
		Log.logMax("LB got the request ... ");
		ServerCalculator server_choosen = getServer(c);
		if(server_choosen != null)
			return server_choosen.pi(digits,type,c);
		else
			return null;

	}
	
}
