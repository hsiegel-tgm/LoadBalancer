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

import main.Log;
import server.CalculatorImpl;
import server.Server;
import server.ServerCalculator;


public class WeightedRR implements Balancer {
	private HashMap<String, ServerCalculator> m_servers = new HashMap<String,ServerCalculator>();
	private String server_weighted[];
	private int m_iterator;
	
	public WeightedRR(String name,String sp) {
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
			System.out.println("There was a remote exception while exporting the Object:"+e.getMessage());
		}
		Log.logMin("Started Weighted Round Robin LB ... ");
	}
	
	
	private void allocate(){
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
		
		Log.logAlg("Allocation has found out a total amount of "+ number +" units");
		
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
		
		String s= "";
		for(int i =0 ; i< number; i++){
			s = s+ servers[i] + "";
		}
		Log.logAlg(s);

		availiable_servers.clear();
		
		while(m_iterator != 0) {}
		
		server_weighted = servers;
	}
	
	
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
	
	public ServerCalculator chooseServer(){
		if ( m_servers.size() <= 0){
			Log.logMax("There is no server which could handle this request!");
			return null;
		}else{
			ServerCalculator server_choosen = (ServerCalculator) m_servers.get(server_weighted[m_iterator]);
			m_iterator++;
			
			if(m_iterator==server_weighted.length)
				m_iterator = 0;
			
			return server_choosen;
		}
	}
	
	public BigDecimal pi(Type type) throws RemoteException{
		Log.logMax("LB got the request ... ");
		ServerCalculator server_choosen = chooseServer();
		if(server_choosen != null)
			return server_choosen.pi(type);
		else
			return null;
	}
	
	public boolean register(ServerCalculator s,String name) throws RemoteException {
		m_servers.put(name,s);
		Log.logMax("Server " +name+ " registered at LoadBalancer. Current number of servers: "+m_servers.size() );
		Log.logAlg("Calling allocation because "+name+" has registered...");
		allocate();
		return true;
	}

	public boolean unregister(ServerCalculator s,String name) throws RemoteException {
		m_servers.remove(name,s);
		return true;
	}

	public BigDecimal pi(int digits,Type type) throws RemoteException {
		Log.logMax("LB got the request ... ");
		ServerCalculator server_choosen = chooseServer();
		if(server_choosen != null)
			return server_choosen.pi(digits,type);
		else
			return null;
	}
}
