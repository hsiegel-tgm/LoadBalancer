package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import server.Calculator.Type;
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
		String sp = "N"; //default: Y
		int intensity = 5; // NO ?? defsysault: 5sec
		String log_int = "normal"; //default
		String log_alg = "n"; //default: N
		String log_res = "n"; //default: N
		String lb_ip="";
		String lb_name="";
		String name = "loadbalancer";
		int digits = 0;
		int num = 20;
		int num_servers = 5;
		int delay = 1;
		int capacity = 5;
		
		service = read(service,"System: What service do you want to start? (all|server|client|many-servers|many-clients|lb|default)","all","server","client","many-servers","default","many-clients","lb","sys"); //TODO REMOVE SYS!
		
		switch (service) {
		 case "all":
			method = read(method,"System: What load balancing method do you want to use? (wrr|aba|default)","wrr","aba","default");
			type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
			sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
    		num = readInt("System: How many clients do you want to generate?",0,1000);
    		num_servers = readInt("System: How many servers do you want to generate?",0,1000);
    		delay = readInt("System: With which delay should the services be generated? Please enter a number in seconds",-1,100);
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
    		digits = readInt("System: How many digits should be calculated?",0,10000);
            break;
        case "many-servers":
    		sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); //TODO check?
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		num_servers = readInt("System: How many servers do you want to generate?",0,1000);
    		delay = readInt("System: With which delay should the services be generated? Please enter a number in seconds",0,100);
            break;
        case "many-clients":
    		type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); //TODO check?
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		num = readInt("System: How many Clients do you want to generate?",0,1000);
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
		
		 boolean session_per=false;
	 	   if(sp.equalsIgnoreCase("y"))
			   session_per = true;
		   else
			   session_per = false;
		
	 	Log.setSessionLogging(session_per);

	 	Type type_wanted = Type.NORMAL;
	 	
		switch (type) {
		 	case "cpu":
				type_wanted = Type.CPU;
				break;
		 	case "ram":
				type_wanted = Type.RAM;
				break;
		 	case "io":
				type_wanted = Type.IO;
				break;
		 	case "mixed":
				type_wanted = Type.MIXED;
				break;	
		}
	 	   
		System.out.print("You are done. Starting the program.");
		for(int i = 0; i<5; ++i){
			try {
				Thread.sleep(333);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(".");
		}			
		System.out.print("\n");
 	  
	 
		switch (service) {
		 case "all":
			 if(method.equals("wrr")){
	           		new WeightedRR(name,session_per);
	    	 }else{
	           		new AgentBasedAdaptive(name,session_per);
	    	 }
			new SimulateServers("127.0.0.1",name,num_servers,delay);
			new SimulateClients("127.0.0.1",name,num,delay,type_wanted); 
			break;
	            
       case "lb":
    	   	if(method.equals("wrr")){
           		new WeightedRR(name,session_per);
    	   	}else{
           		new AgentBasedAdaptive(name,session_per);
    	   	}
			break;
			
       case "server":
    		new Server(lb_ip, lb_name, capacity, name);
	       	break;
       case "client":    		
   			new Client(lb_ip, lb_name, intensity, name,digits,type_wanted,true);
	        break;
       case "many-servers":
			new SimulateServers(lb_ip,lb_name,num_servers,delay);
	        break;
       case "many-clients":
			new SimulateClients(lb_ip,lb_name,num,delay,type_wanted); 
	   		break;    
       case "sys":
       		new AgentBasedAdaptive("wrr-loadbalancingserver",true);
   			new Client("127.0.0.1","wrr-loadbalancingserver", 2, "Client1",10,Type.NORMAL,true);
    		new Server("127.0.0.1","wrr-loadbalancingserver", 5, "Server1");
    		new Client("127.0.0.1","wrr-loadbalancingserver", 2, "Client2",6,Type.NORMAL,true);
    		new Server("127.0.0.1","wrr-loadbalancingserver", 3, "Server2");
			Log.setSessionLogging(true);

			// new SimulateClients("127.0.0.1","aba-loadbalancingserver",20,1); //starting 4 Clients with an delay of 2000 sec
			// new SimulateServers("127.0.0.1","aba-loadbalancingserver",5,7); //starting 2 Servers with an delay of 7000 sec
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
	
}
