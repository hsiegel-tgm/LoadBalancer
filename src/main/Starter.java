package main;

import server.SimulateServers;
import client.SimulateClients;
import balancer.AgentBasedAdaptive;
import balancer.WeightedRR;

public class Starter {

	public static void main(String arg[]) {
		String policy = "grant{permission java.security.AllPermission;};";
		System.setProperty("java.security.policy", policy.toString());

		if( checkArguments(arg) ) {
			if(arg[1].equals("wrr")){
				new WeightedRR("wrr-loadbalancingserver");
				new SimulateServers("127.0.0.1","wrr-loadbalancingserver",10,0); //starting 10 Servers at the same time
				new SimulateClients("127.0.0.1","wrr-loadbalancingserver",20,0); //starting 20 Clients at the same time
			}
			else{
				new AgentBasedAdaptive("aba-loadbalancingserver");
				new SimulateClients("127.0.0.1","aba-loadbalancingserver",20,1); //starting 4 Clients with an delay of 2000 sec
				new SimulateServers("127.0.0.1","aba-loadbalancingserver",5,7); //starting 2 Servers with an delay of 7000 sec
			}
			
			//new Server("127.0.0.1","wrr-loadbalancingserver",2,"Server1");
			//new Server("127.0.0.1","wrr-loadbalancingserver",3,"Server2");
			//new Client("127.0.0.1","wrr-loadbalancingserver",5000,"Client1");
			//new Client("127.0.0.1","wrr-loadbalancingserver",2500,"Client2");
		}
		else{
			System.out.println("Please call the program with some program parameters: \n -m method{wrr|aba}  ");
			System.exit(-1);
		}
	}

	public static boolean checkArguments(String arg[]) {
		// -m method{wrr|aba} 
		if(arg.length >= 2 && arg[0].equals("-m") == true && arg[1].length() > 0){
			String method = arg[1];
			if (method.equalsIgnoreCase("wrr") || method.equalsIgnoreCase("aba")  )
				return true;
		}
		return false;
	}

}
