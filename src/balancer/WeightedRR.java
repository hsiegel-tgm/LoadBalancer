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

public class WeightedRR implements Balancer {
	private HashMap<String, ServerCalculator> m_servers = new HashMap<String, ServerCalculator>();
	private HashMap<String, String> m_session = new HashMap<String, String>();

	private String server_weighted[];
	private int m_iterator;
	private boolean session_persistance = false;

	public WeightedRR(String name, boolean sp) {
		session_persistance = sp;
		
		Log.debug("session persistance:" +session_persistance);
		Balancer x;
		// Exporting stub
		try {
			x = (Balancer) UnicastRemoteObject.exportObject(this, 0);

			// getting the registry
			Registry registry = LocateRegistry.createRegistry(1099);

			// binding Balancer
			registry.bind(name, x);
		} catch (AlreadyBoundException e) {
			System.out
					.println("Load Balancer already bound, can not bound it again");
		} catch (RemoteException e) {
			System.out
					.println("There was a remote exception while exporting the Object:"
							+ e.getMessage());
		}
		Log.logMin("Started Weighted Round Robin LB ... ");
	}

	private void allocate() {
		HashMap<String, Integer> availiable_servers = new HashMap<String, Integer>();

		for (Entry entry : m_servers.entrySet()) {
			ServerCalculator obj = (ServerCalculator) entry.getValue();
			try {
				availiable_servers.put(entry.getKey().toString(),
						obj.getWeight());
			} catch (RemoteException e) {
				Log.error("There was an remote exception when communication with one of the servers",e);
			}
		}

		int number = 0;
		for (Entry entry : availiable_servers.entrySet()) {
			Integer obj = (Integer) entry.getValue();
			number += obj;
		}

		Log.logAlg("Allocation has found out a total amount of " + number
				+ " units");

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

		String s = "";
		for (int i = 0; i < number; i++) {
			s = s + servers[i] + " ";
		}
		Log.logAlg(s);

		availiable_servers.clear();

		while (m_iterator != 0) {
		}

		server_weighted = servers;
	}

	private int getNumbers() {
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

	public ServerCalculator chooseServer(String c) throws RemoteException{
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
				choosen_server = (ServerCalculator) m_servers.get(server_weighted[m_iterator]);
				m_iterator++;
					
				if(m_iterator==server_weighted.length)
						m_iterator = 0;
				if(!m_session.containsKey(c)){
					m_session.put(c, choosen_server.getName());
				}
				Log.logSession(c+" is using the service for the first time. His choosen server is "+choosen_server.getName(),2);

			}
			return choosen_server;
		}
	}

	public boolean register(ServerCalculator s, String name)
			throws RemoteException {
		m_servers.put(name, s);
		Log.logMax("Server " + name
				+ " registered at LoadBalancer. Current number of servers: "
				+ m_servers.size());
		Log.logAlg("Calling allocation because " + name + " has registered...");
		allocate();
		return true;
	}

	public boolean unregister(ServerCalculator s, String name)
			throws RemoteException {
		m_servers.remove(name, s);
		return true;
	}

	public BigDecimal pi(Type type, String c) throws RemoteException {
		Log.logMax("LB got an request from " + c);
		ServerCalculator server_choosen = chooseServer(c);
		if (server_choosen != null)
			return server_choosen.pi(type, c);
		else
			return null;
	}

	public BigDecimal pi(int digits, Type type, String c)
			throws RemoteException {
		Log.logMax("LB got an request from " + c);
		ServerCalculator server_choosen = chooseServer(c);
		if (server_choosen != null)
			return server_choosen.pi(digits, type, c);
		else
			return null;
	}
}
