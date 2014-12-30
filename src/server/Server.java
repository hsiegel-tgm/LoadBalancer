package server;

import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import main.Log;
import balancer.Balancer;
import balancer.Register;
import client.Client;

public class Server implements ServerCalculator{

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
			Log.error("There was a remote error, could not find registry on "+loadbalancerIP);
		} catch (NotBoundException e) {
			Log.error("Server not bound: "+loadbalancerName);
			System.exit(-1);
		}
		
		ServerCalculator x;
		
		try {
			x = (ServerCalculator) UnicastRemoteObject.exportObject(this, 0);
			
			m_balancer.register(x, servername);

		} catch (RemoteException e) {
			Log.error("There was an remote exception: " + e.getMessage());
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

	public BigDecimal pi(Type type) throws RemoteException {
		Log.logAlg(m_servername+": Just got a request!");
		return m_calc.pi(type);
	}

	public BigDecimal pi(int digits, Type type) throws RemoteException {
		Log.logAlg(m_servername+": Just got a request!");
		return m_calc.pi(digits,type);
	}
}
