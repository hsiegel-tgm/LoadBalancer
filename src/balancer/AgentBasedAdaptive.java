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
	
	public AgentBasedAdaptive(String name,boolean sp) {
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
	
	private ServerCalculator getServer() throws RemoteException{
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
		
		return m_servers.get(servers_capacities);
	}
		
	
	public BigDecimal pi(Type type,Client c) {
		Log.logMax("LB got the request ... ");
		
		if ( m_servers.size() <= 0){
			Log.logMax("There is no server which could handle this request!");
			return new CalculatorImpl().pi(type,c); //TODO is this the right thing to do??
		}else{
			
			ServerCalculator server_choosen =null;
			try {
				server_choosen = getServer();
				return server_choosen.pi(type,c);
			} catch (RemoteException e1) {
				Log.error("There was an problem while communicating with the Servers",e1);
				return null;
			}
		}
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

	public BigDecimal pi(int digits,Type type,Client c) throws RemoteException {
		Log.logMax("LB got the request ... ");
		ServerCalculator server_choosen = getServer();
		if(server_choosen != null)
			return server_choosen.pi(digits,type,c);
		else
			return null;

	}
	
}
