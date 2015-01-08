package client;

import server.Calculator.Type;

/**
 * The class SimulateClients is simulating clients
 * 
 * @author Hannah Siegel
 * @version 2014-30-12
 *
 */
public class SimulateClients {
	public SimulateClients(String loadbalancerIP, String loadbalancerName,
			int num, int delay_sec, Type type) {

		System.out.print("Starting " + num + " Clients.");
		for (int i = 0; i < 5; ++i) {
			try {
				Thread.sleep(333);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(".");
		}
		System.out.print("\n");

		// starting clients
		for (int i = 0; i < num; ++i) {
			try {
				Thread.sleep(delay_sec * 1000);
			} catch (InterruptedException e) {
			}

			int intensivity_sec = (int) (Math.random() * 10) + 1;

			new Client(loadbalancerIP, loadbalancerName, intensivity_sec,
					"Client" + i, 0, type, false);
		}
	}

	public SimulateClients(String loadbalancerIP, String loadbalancerName, int num, int delay_sec, Type type, int intensity, int digits) {

		System.out.print("Starting " + num + " Clients.");
		for (int i = 0; i < 5; ++i) {
			try {
				Thread.sleep(333);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.print(".");
		}
		System.out.print("\n");

		// starting clients
		for (int i = 0; i < num; ++i) {
			try {
				Thread.sleep(delay_sec * 1000);
			} catch (InterruptedException e) {
			}

			new Client(loadbalancerIP, loadbalancerName, intensity,"Client" + i, digits, type, false);
		}
	}
}
