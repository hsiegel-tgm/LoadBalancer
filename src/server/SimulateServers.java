package server;

/**
 * The class SimulateServers is simply starting some Servers, in order to simulate them
 * 
 * @author Hannah Siegel
 * @version 2014-12-30
 */
public class SimulateServers {

	/**
	 * Simulate different random servers
	 * 
	 * @param loadbalancerIP
	 * @param loadbalancerName
	 * @param num
	 * @param delay_sec
	 */
	public SimulateServers(String loadbalancerIP, String loadbalancerName,int num,int delay_sec) {
		//Start Output
		System.out.print("Starting "+num+" Servers.");
		
		for(int i = 0; i<5; ++i){
			try {
				Thread.sleep(333);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(".");
		}			
		System.out.print("\n");
		
		for(int i = 0; i<num;++i){
			
			try {
				Thread.sleep(delay_sec*1000);
			} catch (InterruptedException e) {
			}
			
			//calculate rand capacity
			int capacity = (int)(Math.random()*10)+1;
			
			//make server
			new Server(loadbalancerIP,loadbalancerName,capacity,"Server"+i);
		}
	}
	
	/**
	 * Simulated fixed amount
	 * 
	 * @param loadbalancerIP
	 * @param loadbalancerName
	 * @param num
	 * @param delay_sec
	 * @param capacity
	 */
	public SimulateServers(String loadbalancerIP, String loadbalancerName,int num,int delay_sec, int capacity) {
		//Start Output
		System.out.print("Starting "+num+" Servers.");
		
		for(int i = 0; i<5; ++i){
			try {
				Thread.sleep(333);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(".");
		}			
		System.out.print("\n");
		
		
		for(int i = 0; i<num;++i){
			try {
				Thread.sleep(delay_sec*1000);
			} catch (InterruptedException e) {}
			
			//make server
			new Server(loadbalancerIP,loadbalancerName,capacity*(i%10),"Server"+i);
		}
	}
}
