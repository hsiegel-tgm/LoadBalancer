package balancer;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.Server;


public class WeightedRR implements Balancer {
	private HashMap<String, Server> m_servers = new HashMap<String,Server>();
	private static final Logger log = Logger.getLogger( WeightedRR.class.getName() );

	public WeightedRR(String name){
		log.log( Level.SEVERE, "oh oh" );
		log.info("ad");
		
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
			System.out.println("There was a remote exception while exporting the Object:");
			e.printStackTrace();
		}
	}
	
	public float pi() {
		System.out.println("Calculated pi...");
		return 0;
	}

	public void register(Server s,String name) {
		m_servers.put(name,s);
	}

	public void unregister(Server s,String name) {
		m_servers.replace(name,s);
	}

	public float pi_cpu() {
		return 0;
	}

	public float pi_io() {
		return 0;
	}

	public float pi_ram() {
		return 0;
	}

	public float pi_sessionPers() {
		return 0;
	}
}
