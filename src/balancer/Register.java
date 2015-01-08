package balancer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import server.Calculator;
import server.Server;
import server.ServerCalculator;

/**
 * @author Hannah Siegel
 * @version 2014-12-30
 *
 */
public interface Register extends Serializable {
	// used to register new servers at the balancer
	public boolean register(ServerCalculator c,String name)  throws RemoteException;
	// used to unregister a server
	public boolean unregister(ServerCalculator c,String name)  throws RemoteException;
}
