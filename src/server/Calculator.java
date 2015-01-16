package server;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import client.Client;

/**
 * @author Hannah Siegel
 * @version 2014-30-12
 *
 */
public interface Calculator extends java.rmi.Remote {
	public enum Type {
	    NORMAL, CPU, RAM, IO, MIXED
	}
	
	/**
	 * @param type - cpu / ram / io / mixed / noting
	 * @param clientname
	 * @return
	 * @throws RemoteException
	 */
	public BigDecimal pi(Type type,String clientname,int id) throws RemoteException;
	/**
	 * Calculates pi
	 * 
	 * @param digits
	 * @param type - cpu / ram / io / mixed / noting
	 * @param clientname
	 * @return
	 * @throws RemoteException
	 */
	public BigDecimal pi(int digits,Type type,String clientname,int id) throws RemoteException;
}
