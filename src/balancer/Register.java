package balancer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import server.Calculator;
import server.Server;
import server.ServerCalculator;

public interface Register extends Serializable {
	public void register(ServerCalculator c,String name)  throws RemoteException;
	public void unregister(ServerCalculator c,String name)  throws RemoteException;
}
