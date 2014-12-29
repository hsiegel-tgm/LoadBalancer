package main;

import server.Server;
import server.SimulateServers;
import client.Client;
import client.SimulateClients;
import balancer.WeightedRR;

public class Starter {

	public static void main(String arg[]) {
		String policy = "grant{permission java.security.AllPermission;};";
		System.setProperty("java.security.policy", policy.toString());

		if( checkArguments(arg) ) {
			if(arg[1].equals("wrr")){
				new WeightedRR("wrr-loadbalancingserver");
				new SimulateServers("127.0.0.1","wrr-loadbalancingserver",10);
				new SimulateClients("127.0.0.1","wrr-loadbalancingserver",20);

			}
			else{
				new AgendBasedAdaptive("aba-loadbalancingserver");
				new SimulateServers("127.0.0.1","aba-loadbalancingserver",10);
				new SimulateClients("127.0.0.1","aba-loadbalancingserver",20);
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
