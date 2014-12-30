package server;

import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import main.Log;
import balancer.Balancer;
import balancer.Register;
import client.Client;

public class Server implements ServerCalculator{
	private HashMap<String, Integer> m_session_clients = new HashMap<String,Integer>();

	private final int m_weight;
	private Balancer m_balancer;
	private String m_servername;
	private CalculatorImpl m_calc;
	
	public Server(String loadbalancerIP, String loadbalancerName, int weight, String servername ) {
		m_servername = servername;
		m_weight = weight;
		m_calc = new CalculatorImpl();
		
		try{
			Registry registry = LocateRegistry.getRegistry(loadbalancerIP,1099);
			m_balancer = (Balancer) registry.lookup(loadbalancerName);
			
		} catch (RemoteException e) {
			Log.error("There was a remote error, could not find registry on "+loadbalancerIP,e);
		} catch (NotBoundException e) {
			Log.error("Server not bound: "+loadbalancerName,e);
			System.exit(-1);
		}
		
		ServerCalculator x;
		
		try {
			x = (ServerCalculator) UnicastRemoteObject.exportObject(this, 0);
			
			m_balancer.register(x, servername);

		} catch (RemoteException e) {
			Log.error("There was an remote exception.",e);
		}
		Log.log("Started " +m_servername+ " with the weight "+m_weight+ " ... ");
	}

	public int getWeight() {
		return m_weight;
	}

	public int getCurrentWeight() {
		int weight = (int)(Math.random()*100)+1;
		Log.logAlg(m_servername + "has the load: "+weight);
		return weight; //TODO
	}

	public BigDecimal pi(Type type,String c) throws RemoteException {
		Log.logAlg(m_servername+" just got a request from "+c);
		if(m_session_clients.containsKey(c)){
			int digits = m_session_clients.get(c);
			Log.logSession(m_servername+" got an request from "+c+" . He didn't specify the digits, so he took "+digits+ "digits",1);
			return m_calc.pi(digits,type,c);
		}			
		Log.logSession(m_servername+" got an request from "+c+" . It was his first request and therefore he couldnt specify any digits.",2);
		return m_calc.pi(type,c);

	}

	public BigDecimal pi(int digits, Type type,String c) throws RemoteException {
		Log.logAlg(m_servername+" just got a request from "+c);

		int new_digits;
		if(m_session_clients.containsKey(c)){
			int v = m_session_clients.get(c).intValue();
			new_digits = (v+(2*digits))/3;
			m_session_clients.put(c, new_digits);
		}else{
			m_session_clients.put(c, digits);
			new_digits = digits;
		}
		Log.logSession(m_servername+" just added an session information about "+c+": He wanted "+new_digits +" digits.",2);
		return m_calc.pi(digits,type,c);
	}
	
	public String getName(){
		return m_servername;
	}
}
