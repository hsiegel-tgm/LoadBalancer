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

/**
 * Start class. Starts a parameter questioning for the user
 * 
 * @author Hannah Siegel
 * @version 2014-12-30
 *
 */
public class Starter {
	
	public static void main(String arg[]) throws IOException {
		String policy = "grant{permission java.security.AllPermission;};";
		System.setProperty("java.security.policy", policy.toString());
		new Starter();
	}
	
	/**
	 * Starts the input
	 */
	public Starter(){
		try {
			getParameters();
		} catch (IOException e1) {
			Log.error("There was an problem: ", e1);
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
		String log_sess = "n"; //default: N
		String lb_ip="127.0.0.1";
		String lb_name="loadbalancer";
		String name = "loadbalancer";
		int digits = 0;
		int num = 20;
		int num_servers = 5;
		int delay = 1;
		int capacity = 5;
		
		service = read(service,"System: What service do you want to start? (all|server|client|compare|many-servers|many-clients|lb|default)","all","server","client","many-servers","compare","default","many-clients","lb");
		
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
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); 
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		name =  readNormal("System: What should the service be called?");
    		capacity = readInt("System: With which capacity should the server be initialized?",0,10);
        	break;
        case "client":
    		lb_ip = readNormal("System: Please put in the load Balancer's IP"); 
    		lb_name = readNormal("System: What is the loadbalancers lookup name");
    		name =  readNormal("System: What should the service be called?");
    		type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
    		intensity = readInt("System: With which intensity should you client send requests? Please enter a number in seconds",0,100);
    		digits = readInt("System: How many digits should be calculated?",0,10000);
            break;
        case "many-servers":
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
        case "comapre":
			method = read(method,"System: What load balancing method do you want to use? (wrr|aba|default)","wrr","aba","default");
    		sp = read(sp,"System: Do you want to use the session persistance? (Y|n|default)","Y","n","default");
    		type = read(type,"System: What type of service do you want to use? (normal|cpu|ram|io|mixed|default)","normal","cpu","ram","io","mixed","default");
            break; 
        }
		
		log_int = read(log_int,"System: With which intensity should be logged? (min|normal|max|default)","min","normal","max","default");
		log_alg = read(log_alg,"System: Should the algotithms be logged? (Y|n|default)","Y","n","default");
		log_res = read(log_res,"System: Should the results be logged? (Y|n|default)","Y","n","default");

		//setting the Logging
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
	 	   if(sp.equalsIgnoreCase("y")){
			   session_per = true;
			   log_sess = read(log_res,"System: Should the session persistance be logged? (Y|n|default)","Y","n","default");
				if(log_sess.equalsIgnoreCase("y"))
					Log.setSessionLogging(true);
				else
					Log.setSessionLogging(false);
	 	   }
		   else
			   session_per = false;
		
	 	 //type
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
	 	  
		//nice output
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
 	  
		// start the services
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
	       	
       case "compare":
    	   name = "compare";
    	   
    	   if(method.equals("wrr")){
          		new WeightedRR(name,session_per);
    	   }else{
          		new AgentBasedAdaptive(name,session_per);
    	   }
    		new SimulateServers("127.0.0.1",name,10, delay, 1);
    	    new SimulateClients("127.0.0.1",name,500,delay,type_wanted,intensity,digits); 
	       	break;
	       	
       case "client":    		
   			new Client(lb_ip, lb_name, intensity, name,digits,type_wanted,true);
	        break;
       
       case "many-servers":
			new SimulateServers(lb_ip,lb_name,num_servers,delay);
	        break;
       
       case "many-clients":
    	   	//starting clients
			new SimulateClients(lb_ip,lb_name,num,delay,type_wanted,0,0); 
	   		break;    
		}
	}
	
	/**
	 * The method read is reading in a line, and checks it.
	 * 
	 * @param default_string
	 * @param message
	 * @param strings
	 * @return answer
	 * @throws IOException
	 */
	public String read(String default_string, String message,String... strings) throws IOException{
		boolean ok = false;
		String answer="";
		
		//asking as long as the answer was permitted
		while(!ok){
			System.out.println(message);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String line = br.readLine();
			
			//checking if the String is 'permitted'
			for ( String s : strings ) {
			   if(line.equalsIgnoreCase(s)){
				   ok = true;
				   answer = line;
				   if(line.equalsIgnoreCase("default"))
					   answer = default_string;
			   }
			}             
		}
		return answer.toLowerCase();
	}
	
	/**
	 * The method reads in a String without checking it
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
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
	
	/**
	 * The method reads an integer value from the user and checks if it is between a specific range
	 * 
	 * @param message
	 * @param from
	 * @param to
	 * @return
	 * @throws IOException
	 */
	public int readInt(String message,int from, int to) throws IOException{
		boolean ok = false;
		int answer=0;
		while(!ok){
			System.out.println(message + ". The range must be between "+(from+1)+" to "+to);
			
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
