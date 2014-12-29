package balancer;

import java.math.BigDecimal;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import main.Log;
import server.Server;
import server.ServerCalculator;


public class WeightedRR implements Balancer {
	private HashMap<String, ServerCalculator> m_servers = new HashMap<String,ServerCalculator>();

	public WeightedRR(String name){
		
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
		Log.info("Started Weighted Round Robin LB ... ");

	}
	
	public BigDecimal pi() {
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
	}

	public void register(ServerCalculator s,String name) throws RemoteException {
		m_servers.put(name,s);
		Log.info("Server " +name+ " registered at LoadBalancer. Current number of servers: "+m_servers.size() );
	}

	public void unregister(ServerCalculator s,String name) throws RemoteException {
		m_servers.replace(name,s);
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
