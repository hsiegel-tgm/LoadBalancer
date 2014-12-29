package client;


public class SimulateClients {
	public SimulateClients(String loadbalancerIP, String loadbalancerName,int num)  {
		for(int i = 0; i<num;++i){
			int intensivity_sec = (int)(Math.random()*10)+1;
			new Client(loadbalancerIP,loadbalancerName,intensivity_sec*1000,"Client"+i);
		}
	}
}
