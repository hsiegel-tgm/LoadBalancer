package balancer;

import java.math.BigDecimal;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;

import main.Log;
import server.Calculator;
import server.CalculatorImpl;
import server.Server;
import server.ServerCalculator;

public class AgentBasedAdaptive implements Balancer{
	private HashMap<String, ServerCalculator> m_servers = new HashMap<String,ServerCalculator>();
	// private String server_weighted[];
	// private int m_iterator;
	
	public AgentBasedAdaptive(String name) {
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
		Log.info("Started Agent Based Adaptive LB ... ");
	}
	
	/* public BigDecimal pi() {
		Log.debug("LB got the request");
		if(m_servers.size()>0){
			for (Entry entry : m_servers.entrySet()) {
				 //getting key
				 String key = entry.getKey().toString();
				 ServerCalculator obj = (ServerCalculator) entry.getValue();
				 try {
					 Log.debug("Sending the request to the server ... name: "+key);
					return obj.pi();
				} catch (RemoteException  e) {
					e.printStackTrace();
				}
			}
			Log.debug("Calculated pi...");
		}
		else{
			Log.info("Sorry. But there is no server availiable.");
		}
		return null;
	} */
	
	/* private void allocate(){
		HashMap<String, Integer> availiable_servers = new HashMap<String,Integer>();

		for (Entry entry : m_servers.entrySet()) {
			 ServerCalculator obj = (ServerCalculator) entry.getValue();
			 try {
				 availiable_servers.put(entry.getKey().toString(),  obj.getWeight()); 
			} catch (RemoteException e) {
				Log.error("There was an remote exception when communication with one of the servers");
			}
		}
		
		int number = 0;
		for (Entry entry : availiable_servers.entrySet()) {
			Integer obj = (Integer) entry.getValue();
			number += obj;
		}
		
		Log.debug("Allocation has found out a total amount of "+ number +" units");
		
		int servers[] = new int[number];
		
		int already_allocated = 0;
		
		for(int j = 0 ; j < number ; ++j ){
			if()
			servers[j]
		}
		
	} */
	
	
	/*private void allocate(){
		HashMap<String, Integer> availiable_servers = new HashMap<String,Integer>();

		for (Entry entry : m_servers.entrySet()) {
			 ServerCalculator obj = (ServerCalculator) entry.getValue();
			 try {
				 availiable_servers.put(entry.getKey().toString(),  obj.getWeight()); 
			} catch (RemoteException e) {
				Log.error("There was an remote exception when communication with one of the servers");
			}
		}
		
		int number = 0;
		for (Entry entry : availiable_servers.entrySet()) {
			Integer obj = (Integer) entry.getValue();
			number += obj;
		}
		
		Log.debug("Allocation has found out a total amount of "+ number +" units");
		
		String servers[] = new String[number];
		
		int already_allocated = 0;
		
		while (already_allocated < number ){
			for (Entry entry : availiable_servers.entrySet()) {
				Integer capcity = (Integer) entry.getValue();
				if(capcity>0){
					String key = entry.getKey().toString();
					servers[already_allocated] = key;
					already_allocated++;
				}
			}
			
			HashMap<String, Integer> availiable_servers2 = new HashMap<String,Integer>();

			for (Entry entry : availiable_servers.entrySet()) {
				Integer capcity = (Integer) entry.getValue();
				availiable_servers2.put(entry.getKey().toString(),capcity-1);
			}
			availiable_servers = availiable_servers2;
		}
		
		for(int i =0 ; i< number; i++){
			Log.debug(servers[i]);
		}
		availiable_servers.clear();
		
		while(m_iterator != 0) {}
		
		server_weighted = servers;
	}*/
	
	
	private int getNumbers(){
		int num = 0;
		for (Entry entry : m_servers.entrySet()) {
			 ServerCalculator obj = (ServerCalculator) entry.getValue();
			 try {
				num += obj.getWeight();
			} catch (RemoteException e) {
				Log.error("There was an remote exception when communication with one of the servers");
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
		
	
	public BigDecimal pi() {
		Log.debug("LB got the request ... ");
		
		if ( m_servers.size() <= 0){
			Log.info("There is no server which could handle this request!");
			return new CalculatorImpl().pi(); //TODO is this the right thing to do??
		}else{
			
			ServerCalculator server_choosen =null;
			try {
				server_choosen = getServer();
				return server_choosen.pi();
			} catch (RemoteException e1) {
				Log.error("There was an problem while communicating with the Servers");
				return null;
			}
		}
	}
	
	public boolean register(ServerCalculator s,String name) throws RemoteException {
		m_servers.put(name,s);
		Log.info("Server " +name+ " registered at LoadBalancer. Current number of servers: "+m_servers.size() );
		return true;
	}

	public boolean unregister(ServerCalculator s,String name) throws RemoteException {
		m_servers.remove(name,s);
		return true;
	}

	public BigDecimal pi_cpu() throws RemoteException {
		return null;
	}

	public BigDecimal pi_io() throws RemoteException {
		return null;
	}

	public BigDecimal pi_ram()  throws RemoteException {
		return null;
	}

	public BigDecimal pi_sessionPers()  throws RemoteException {
		return null;
	}

	

	@Override
	public BigDecimal pi(int digits) throws RemoteException {
		return null;
	}
	
}
