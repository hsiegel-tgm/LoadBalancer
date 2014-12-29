package server;

public class SimulateServers {

	public SimulateServers(String loadbalancerIP, String loadbalancerName,int num) {
		for(int i = 0; i<num;++i){
			int capacity = (int)(Math.random()*10)+1;
			new Server(loadbalancerIP,loadbalancerName,capacity,"Server"+i);
		}
	}
}
