package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import server.Server;
import server.SimulateServers;
import client.Client;
import client.SimulateClients;
import balancer.AgentBasedAdaptive;
import balancer.WeightedRR;

public class Starter {

	public static void main(String arg[]) throws IOException {
		String policy = "grant{permission java.security.AllPermission;};";
		System.setProperty("java.security.policy", policy.toString());
		new Starter(arg);
	}
	
	public Starter(String arg[]){
		try {
			getParameters();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void getParameters() throws IOException{
		String service = "all"; //default: all
		String method = "wrr"; //default: wrr
		String type = "normal"; //default: normal
		String sp = "Y"; //default: Y
		int intensity = 5; // NO ?? default: 5sec
		String log_int = "normal"; //default
		String log_alg = "n"; //default: N
		String log_res = "n"; //default: N
		String lb_ip="";
		String lb_name="";
		String name = "";
		int num = 20;
		int delay = 1;
		int capacity = 5;
		
		service = read(service,"System: What service do you want to start? (all|server|client|many-servers|many-clients|lb|default)","all","server","client","many-servers","default","many-clients","lb","sys"); //TODO REMOVE SYS!
		
		switch (service) {
		 case "all":
			method = read(method,"System: What load balancing method do you want to use? (wrr|aba|default)","wrr","aba","default");
			type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
			sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
			 break;
	            
        case "lb":
			method = read(method,"System: What load balancing method do you want to use? (wrr|aba|default)","wrr","aba","default");
    		sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
    		name =  readNormal("System: What should the service be called?");
        	break;
        case "server":
    		sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); //TODO check?
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		name =  readNormal("System: What should the service be called?");
    		capacity = readInt("System: With which capacity should the server be initialized?",0,10);
        	break;
        case "client":
    		type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); //TODO check?
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		name =  readNormal("System: What should the service be called?");
    		intensity = readInt("System: With which intensity should you client send requests? Please enter a number in seconds",0,100);
            break;
        case "many-servers":
    		sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); //TODO check?
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		num = readInt("System: How many of these services do you want to generate?",0,1000);
    		delay = readInt("System: With which delay should the services be generated? Please enter a number in seconds",0,100);
            break;
        case "many-clients":
    		type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); //TODO check?
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		num = readInt("System: How many of these services do you want to generate?",0,1000);
    		delay = readInt("System: With which delay should the services be generated? Please enter a number in seconds",0,100);
            break;    
        }
		
		log_int = read(log_int,"System: With which intensity should be logged? (min|normal|max|default)","min","normal","max","default");
		
		log_alg = read(log_alg,"System: Should the algotithms be logged? (Y|n|default)","Y","n","default");

		log_res = read(log_res,"System: Should the results be logged? (Y|n|default)","Y","n","default");
		
		if(log_res.equalsIgnoreCase("y"))
			Log.setResultLogging(true);
		else
			Log.setResultLogging(false);
		
		if(log_alg.equalsIgnoreCase("y"))
			Log.setAlgorithmLogging(true);
		else
			Log.setAlgorithmLogging(false);

		Log.setIntensity(log_int);
		
		System.out.print("You are done. Starting the program.");
		
		
		switch (service) {
		 case "all":
			 if(method.equals("")){
	           		new WeightedRR(name,sp);
	    	 }else{
	           		new AgentBasedAdaptive(name,sp);
	    	 }
			new SimulateClients("127.0.0.1",name,num,delay); //starting 4 Clients with an delay of 2000 sec
			new SimulateServers("127.0.0.1",name,num/10,delay); //starting 2 Servers with an delay of 7000 sec

			// type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");

			break;
	            
       case "lb":
    	   	if(method.equals("")){
           		new WeightedRR(name,sp);
    	   	}else{
           		new AgentBasedAdaptive(name,sp);
    	   	}
			break;
       case "server":
    		new Server(lb_ip, lb_name, capacity, name);
	   		// TODO?? sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
	       	break;
       case "client":
   			new Client(lb_ip, lb_name, intensity, name);
	   		// TODO type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
	        break;
       case "many-servers":
			new SimulateServers(lb_ip,lb_name,num,delay); //starting 2 Servers with an delay of 7000 sec
	   		// TODO sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
	        break;
       case "many-clients":
			new SimulateClients(lb_ip,lb_name,num,delay); //starting 4 Clients with an delay of 2000 sec
	   		// TODO type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
	   		break;    
       case "sys":
       		new AgentBasedAdaptive("aba-loadbalancingserver","");
			new SimulateClients("127.0.0.1","aba-loadbalancingserver",20,1); //starting 4 Clients with an delay of 2000 sec
			new SimulateServers("127.0.0.1","aba-loadbalancingserver",5,7); //starting 2 Servers with an delay of 7000 sec
           break;
		}
	}
	
	public String read(String default_string, String message,String... strings) throws IOException{
		boolean ok = false;
		String answer="";
		while(!ok){
			System.out.println(message);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line = br.readLine();
			
			for ( String s : strings ) {
			   if(line.equalsIgnoreCase(s)){
				   ok = true;
				   answer = line;
				   if(line.equalsIgnoreCase("default")){
					   answer = default_string;
				   }
			   }
			}             
		}
		return answer.toLowerCase();
	}
	
	public String readNormal(String message) throws IOException{
		boolean ok = false;
		String answer="";
		while(!ok){
			System.out.println(message);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line = br.readLine();
			if(line.length()>=1){
				ok = true;
				answer = line;
			}
		}
		return answer;
	}
	
	public int readInt(String message,int from, int to) throws IOException{
		boolean ok = false;
		int answer=0;
		while(!ok){
			System.out.println(message + ". The range must be between "+from+1+" to "+to);
			
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line = br.readLine();
			int num = 0;
			try{
				 num = Integer.parseInt(line);
			}catch(java.lang.NumberFormatException nfe){
				continue;
			}

			if(num > from && num <= to){
				answer =  num;
				ok = true;
			}             
		}
		return answer;
	}
	
	
		
//		if( checkArguments(arg) ) {
//			try {
//				getLoggingData();
//			} catch (IOException e) {
//				System.exit(-1);
//			}
//			
//			Log.debug("ready?");
//			
//		}else{
//			printUsage();
//		}
//		
		
//		
//		
//		
//		System.exit(-1);
//		
//		if( checkArguments(arg) ) {
//			if(arg[1].equals("wrr")){
//				new WeightedRR("wrr-loadbalancingserver","");
//				new SimulateServers("127.0.0.1","wrr-loadbalancingserver",10,0); //starting 10 Servers at the same time
//				new SimulateClients("127.0.0.1","wrr-loadbalancingserver",20,0); //starting 20 Clients at the same time
//			}
//			else{
//				new AgentBasedAdaptive("aba-loadbalancingserver");
//				new SimulateClients("127.0.0.1","aba-loadbalancingserver",20,1); //starting 4 Clients with an delay of 2000 sec
//				new SimulateServers("127.0.0.1","aba-loadbalancingserver",5,7); //starting 2 Servers with an delay of 7000 sec
//			}
//		}
//		
//	}
//	
	
	
//	
//
//	public static boolean checkArguments(String arg[]) {
//		if(arg.length >= 2 && arg[0].equals("-m") == true && arg[1].length() > 0){
//			String method = arg[1];
//			if (method.equalsIgnoreCase("wrr") || method.equalsIgnoreCase("aba")  )
//				return false;
//		}
//		return false;
//	}
//	
//	public static void printUsage() {
//		System.out.println("The following parameters are availiable: \n -s -m -t -sp -i -li -la -lr -D -C -L");
//		
//		System.out.println("\n   -s service{ss|sc|s|c|lb|all}         - specifies the service to be started. all is starting a load balancer, clients and a server");
//		System.out.println("   -m  method{wrr|aba}                    - which method should be used (Weighted Round Robin or Agent Based Adaptive)");
//		System.out.println("   -t  type{normal|cpu|ram|io|mixed}  	  - The type/service which should be done. Mixed is starting all the services randomly ");
//		System.out.println("   -sp session-persistance{true|false}    - If session persistance should be done");
//		System.out.println("   -i  intensity{low|normal|high}         - The intensity with which the clients will revoke requests");
//		System.out.println("   -li logging-intensity{min|normal|max}  - Logging intensity (min is only logging major things, max everything)");
//		System.out.println("   -la log-algorithm{true|false}          - True when the Load Balancing Algorithm should be logged, false otherwise");
//		System.out.println("   -lr log-results{true|false}            - True when the results(pi) should be printed out");
//		System.out.println("   -D                                     - Default config will be used for the parameters that have not been given (only the -s and the -m parameter if using a loadbalancer is necessary)");
//		System.out.println("   -C                                     - System dialog will be used to ask for the parameters that have not been given");
//		System.out.println("   -L                                     - Default config will be used for the logging parameters");
//
//		System.out.println(" \n \n");
//		System.out.println(" For Example: \n");
//		System.out.println("-s all -m wrr -t normal -sp false -i low -li min -la true -lr false");
//		System.out.println("-s all -m aba -t cpu -sp true -i high -L");
//		System.out.println("-s lb -m wrr -D");
//		System.out.println("-C");
//	}
}
