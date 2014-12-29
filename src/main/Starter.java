package main;

import balancer.WeightedRR;

public class Starter {

	public static void main(String arg[]) {
		String policy = "grant{permission java.security.AllPermission;};";
		System.setProperty("java.security.policy", policy.toString());
		
		if( checkArguments(arg) ) {
			new WeightedRR("wrr-loadbalancingserver");
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
