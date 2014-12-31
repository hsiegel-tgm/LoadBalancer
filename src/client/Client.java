package client;

import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import server.Calculator.Type;
import main.Log;
import balancer.Balancer;

public class Client implements Runnable {
	private Thread m_thread;
	private Balancer m_balancer;
	private int m_intensity;
	private String m_clientname;
	private int m_digits;
	private Type m_type;

	public Client(String loadbalancerIP, String loadbalancerName, int intensity,String clientname,int digits,Type type,boolean log) {
		this.m_intensity = intensity*1000;
		this.m_clientname = clientname;
		this.m_type = type;
		
		if(digits == 0)
			this.m_digits = (int)((Math.random()*50)+1);
		else
			this.m_digits = digits;
		
		try{
			Registry registry = LocateRegistry.getRegistry(loadbalancerIP,1099);
			m_balancer = (Balancer) registry.lookup(loadbalancerName);
		} catch (RemoteException e) {
			Log.error("There was a remote error, could not find registry on "+loadbalancerIP,e);
		} catch (NotBoundException e) {
			Log.error("Server not bound: "+loadbalancerName,e);
		}
		if(log)
			Log.logMin("Started "+m_clientname + " with the intensity "+m_intensity+ " ... ");
		
		m_thread = new Thread(this);
		m_thread.start();
	}

	public void run() {
		int turns = 0;
		while(true){
			try {
				Thread.sleep(m_intensity);
				BigDecimal pi = null;
				if(turns % 7 == 0)
					pi = m_balancer.pi(m_type,this.getName()); //TODO this oder thread?
				else	
					pi = m_balancer.pi(m_digits,m_type,this.getName()); //TODO this oder thread?
				
				if(pi!= null)
					Log.logRes(m_clientname+ " got a response: " + pi.toEngineeringString());
				else
					Log.warn(m_clientname+" didn't get any response");
				
				turns ++;
				if(turns % 5 == 0){
					int multiplicator = (int)((Math.random()*3))-1;
					int new_digits  = m_digits +  (int)((Math.random()*10)+1)*multiplicator;
					if(new_digits<=0)
						new_digits = 10;
					Log.logSession(m_clientname+" just changed it digits from "+ m_digits +" to "+ new_digits,1);
					m_digits = new_digits;
				}
			} catch (RemoteException e1) {
				Log.error("There was an Remote Exception",e1);
			}
			catch (InterruptedException e) {
				Log.warn("The Thread was interrupted.");
			}
		}
	}
	
	public String getName(){
		return m_clientname;
	}
}
