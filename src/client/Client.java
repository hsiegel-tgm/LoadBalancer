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

/**
 * The class Client is the implementation of an client
 * 
 * @author Hannah Siegel
 * @version 2014-30-12
 *
 */
public class Client implements Runnable {
	private Thread m_thread;
	//Balancer
	private Balancer m_balancer;
	
	private int m_intensity;
	private String m_clientname;
	private int m_digits;
	private Type m_type;

	/**
	 * Constructor of the class, 
	 * 
	 * @param loadbalancerIP 
	 * @param loadbalancerName
	 * @param intensity
	 * @param clientname
	 * @param digits
	 * @param type - normal / cpu / ram / io / mixed
	 * @param log
	 */
	public Client(String loadbalancerIP, String loadbalancerName, int intensity,String clientname,int digits,Type type,boolean log) {
		this.m_intensity = intensity*1000;
		this.m_clientname = clientname;
		this.m_type = type;
		
		if(digits == 0)
			this.m_digits = (int)((Math.random()*100)+1);
		else
			this.m_digits = digits;
		
		//get the balancer
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
		
		//start the thread
		m_thread = new Thread(this);
		m_thread.start();
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		int turns = 0;
		int iterator = 0;

		//possible types
		Type types[] ={Type.NORMAL,Type.CPU,Type.IO,Type.RAM};
		boolean first = true;
		int request_count = 1;
		while(true){
			try {
				//wait
				Thread.sleep(m_intensity);
				
				
				BigDecimal pi = null;
				if(m_type == Type.MIXED){
					//choose a type
					Type current_type = types[iterator];
					
					// every second turn is not giving any digit information
					if(turns % 4 == 0 && !first)
						pi = m_balancer.pi(current_type,this.getName(),request_count); 
					else	
						pi = m_balancer.pi(m_digits,current_type,this.getName(),request_count); 
					iterator++;
					
					if(iterator == types.length)
						iterator = 0;
					
				}else{
					if(turns % 4 == 0 && !first){
						Log.debug(m_clientname+" is calling without info");
						pi = m_balancer.pi(m_type,this.getName(),request_count); 
					}
					else{	
						Log.debug(m_clientname+" is calling with "+m_digits);

						pi = m_balancer.pi(m_digits,m_type,this.getName(),request_count);
					}
				}

				if(pi!= null)
					Log.logRes(m_clientname+ " got a response: " + pi.toEngineeringString());
				else
					Log.warn(m_clientname+" didn't get any response");
				
				//after every 5th turn it is changing its number of digits
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
			first = false;
			request_count++;
		}
	}
	
	/**
	 * @return clients name
	 */
	public String getName(){
		return m_clientname;
	}
}
