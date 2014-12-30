package server;

public class SimulateServers {

	public SimulateServers(String loadbalancerIP, String loadbalancerName,int num,int delay_sec) {
		for(int i = 0; i<num;++i){
			
			try {
				Thread.sleep(delay_sec*1000);
			} catch (InterruptedException e) {
			}
			
			int capacity = (int)(Math.random()*10)+1;
			
			new Server(loadbalancerIP,loadbalancerName,capacity,"Server"+i);
		}
	}
}
