package server;

import java.rmi.RemoteException;

/**
 * ServerCalculator interface
 * 
 * @author Hannah Siegel
 * @version 2014-12-30
 *
 */
public interface ServerCalculator extends Calculator {
	/**
	 * Should return the set weight of a server
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public int getWeight() throws RemoteException;
	
	/**
	 * Returns the current weight
	 * 
	 * @return
	 * @throws RemoteException
	 */
	public int getCurrentWeight() throws RemoteException;
	
	/**
	 * Returns the name of the server
	 * @return
	 * @throws RemoteException
	 */
	public String getName() throws RemoteException;
	
	/**
	 * Returns the digit from one client (saved thru session pers)
	 * 
	 * @param client
	 * @return
	 * @throws RemoteException
	 */
	public int getDigits(String client) throws RemoteException;
	
	/**
	 * Sets the digits from a client
	 * 
	 * @param client
	 * @param digits
	 * @throws RemoteException
	 */
	public void setDigits(String client, int digits) throws RemoteException;
}
