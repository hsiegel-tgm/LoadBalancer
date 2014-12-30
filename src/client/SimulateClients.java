package client;


public class SimulateClients {
	public SimulateClients(String loadbalancerIP, String loadbalancerName,int num,int delay_sec)  {
		for(int i = 0; i<num;++i){
			
			try {
				Thread.sleep(delay_sec*1000);
			} catch (InterruptedException e) {
			}
			
			int intensivity_sec = (int)(Math.random()*10)+1;
			new Client(loadbalancerIP,loadbalancerName,intensivity_sec*1000,"Client"+i);
		}
	}
}
