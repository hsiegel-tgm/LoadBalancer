package client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import main.Log;
import balancer.Balancer;

public class Client implements Runnable {
	private Thread m_thread;
	private Balancer m_balancer;
	private int m_intensity;
	private String m_clientname;
	
	public Client(String loadbalancerIP, String loadbalancerName, int intensity,String clientname) {
		this.m_intensity = intensity;
		this.m_clientname = clientname;
		
		try{
			Registry registry = LocateRegistry.getRegistry(loadbalancerIP,1099);
			m_balancer = (Balancer) registry.lookup(loadbalancerName);
		} catch (RemoteException e) {
			Log.error("There was a remote error, could not find registry on "+loadbalancerIP+ " with the name" + loadbalancerName);
		} catch (NotBoundException e) {
			Log.error("Server not bound: "+loadbalancerName);
		}
		Log.info("Started "+m_clientname + " with the intensity "+m_intensity+ " ... ");
		
		m_thread = new Thread(this);
		m_thread.start();
	}

	public void run() {
		while(true){
			try {
				Thread.sleep(m_intensity);
				Log.debug(m_clientname+ " got a response: " + m_balancer.pi().toEngineeringString());
			} catch (RemoteException e1) {
				Log.error("There was an Remote Exception");
			}
			catch (InterruptedException e) {
				Log.warn("The Thread was interrupted.");
			}
		}
	}

}
