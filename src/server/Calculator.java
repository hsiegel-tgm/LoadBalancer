package server;

import java.math.BigDecimal;
import java.rmi.RemoteException;

public interface Calculator extends java.rmi.Remote {
	public enum Type {
	    NORMAL, CPU, RAM, IO
	}
	public BigDecimal pi_ram() throws RemoteException;
	public BigDecimal pi_cpu() throws RemoteException;
	public BigDecimal pi_io() throws RemoteException;
	public BigDecimal pi_sessionPers() throws RemoteException;
	public BigDecimal pi(Type type) throws RemoteException;
	public BigDecimal pi(int digits,Type type) throws RemoteException;
}
