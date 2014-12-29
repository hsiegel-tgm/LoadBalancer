package balancer;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import server.Calculator;
import server.Server;

public interface Register extends Serializable {
	public void register(Server c,String name)  throws RemoteException;
	public void unregister(Server c,String name)  throws RemoteException;
}
