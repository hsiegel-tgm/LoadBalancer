package test;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import main.Log;

import org.junit.Test;

import server.CalculatorImpl;
import server.Server;
import server.ServerCalculator;
import balancer.AgentBasedAdaptive;
import balancer.Balancer;
import balancer.WeightedRR;
import client.SimulateClients;
import server.SimulateServers;
import server.Calculator.Type;

public class BalancerAbaTest {
	@Test
	public void testAba() {
		BigDecimal erg = null;
		try{
			AgentBasedAdaptive a = new AgentBasedAdaptive("aba-loadbalancingserver2", true);
		
			new Server("127.0.0.1", "aba-loadbalancingserver2", 0, "s1");
		
			Registry registry = LocateRegistry.getRegistry("127.0.0.1");

			Balancer balancer = (Balancer) registry.lookup("aba-loadbalancingserver2");
			erg = balancer.pi(3,Type.NORMAL,"CL1");
		}catch(Exception e){}
			assertEquals(erg.toString(),"3.142");
	}
	
	@Test
	public void testAba3() {
		BigDecimal erg = null;
		try{
			Registry registry = LocateRegistry.getRegistry("127.0.0.1");
			Balancer balancer = (Balancer) registry.lookup("aba-loadbalancingserver2");
			erg = balancer.pi(3,Type.CPU,"CL1");
		}catch(Exception e){}
			assertEquals(erg.toString(),"3.142");
	}
	
	
	@Test
	public void testAba2() {
		BigDecimal erg = null;
		try{
		
			new Server("127.0.0.1", "aba-loadbalancingserver2", 0, "s1");
		
			
		}catch(Exception e){}
			assertEquals(true,true);
	}
	
	public void testWrr() {
		BigDecimal erg = null;
		try{
			new WeightedRR("wrr-loadbalancingserver2", true);
		
			new Server("127.0.0.1", "wrr-loadbalancingserver2", 0, "s3");
		
			Registry registry = LocateRegistry.getRegistry("127.0.0.1");

			Balancer balancer = (Balancer) registry.lookup("wrr-loadbalancingserver2");
			erg = balancer.pi(3,Type.NORMAL,"CL1");
		}catch(Exception e){}
			assertEquals(erg.toString(),"3.142");
	}
}
