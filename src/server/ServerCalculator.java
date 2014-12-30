package server;

import java.rmi.RemoteException;

public interface ServerCalculator extends Calculator {
	public int getWeight() throws RemoteException;
	public int getCurrentWeight() throws RemoteException;
	public String getName() throws RemoteException;

}
