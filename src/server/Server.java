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
			Log.error("There was a remote error, could not find registry on "+loadbalancerIP+ " with the name" + loadbalancerName);
		} catch (NotBoundException e) {
			Log.error("Server not bound: "+loadbalancerName);
		}
		
		ServerCalculator x;
		
		try {
			x = (ServerCalculator) UnicastRemoteObject.exportObject(this, 0);
			
			m_balancer.register(x, servername);

		} catch (RemoteException e) {
			Log.error("There was an remote exception: " + e.getMessage());
		}
		
		Log.info("Started " +m_servername+ " with the weight "+m_weight+ " ... ");
	}

	public int getWeight() {
		return m_weight;
	}

	public int getCurrentWeight() {
		return 0; //TODO
	}

	@Override
	public BigDecimal pi_ram() throws RemoteException {
		return null;
	}

	@Override
	public BigDecimal pi_cpu() throws RemoteException {
		return null;
	}

	@Override
	public BigDecimal pi_io() throws RemoteException {
		return null;
	}

	@Override
	public BigDecimal pi_sessionPers() throws RemoteException {
		return null;
	}

	@Override
	public BigDecimal pi() throws RemoteException {
		Log.debug(m_servername+": Just got a request!");
		return m_calc.pi();
	}

	@Override
	public BigDecimal pi(int digits) throws RemoteException {
		return m_calc.pi(digits);
	}
}
