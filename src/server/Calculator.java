package server;

import java.rmi.RemoteException;

public interface Calculator extends java.rmi.Remote {
	public float pi_ram() throws RemoteException;
	public float pi_cpu() throws RemoteException;
	public float pi_io() throws RemoteException;
	public float pi_sessionPers() throws RemoteException;
	public float pi() throws RemoteException;

	
}
