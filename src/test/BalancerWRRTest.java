package test;

import static org.junit.Assert.assertEquals;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import main.Log;

import org.junit.Test;

import server.CalculatorImpl;
import server.ServerCalculator;
import balancer.Balancer;
import balancer.WeightedRR;

public class BalancerWRRTest {
	
	//@Test
	//public void normalStartup() {
	//	new WeightedRR("wrr-loadbalancingserver");
	 //} 
	
	@Test
	public void testRegister() {
		boolean erg = false;
		try{
			new WeightedRR("wrr-loadbalancingserver");
		
			Registry registry = LocateRegistry.getRegistry("127.0.0.1");
			Balancer b = (Balancer) registry.lookup("wrr-loadbalancingserver");
		
			ServerCalculator x = (ServerCalculator) UnicastRemoteObject.exportObject(new CalculatorImpl(), 0);
		
			erg = b.register(x, "S1");
		
		}catch(Exception e){}
		
		assertEquals(erg,true);
	}


}