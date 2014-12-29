package server;

import java.math.BigDecimal;
import java.rmi.RemoteException;

public interface Calculator extends java.rmi.Remote {
	public BigDecimal pi_ram() throws RemoteException;
	public BigDecimal pi_cpu() throws RemoteException;
	public BigDecimal pi_io() throws RemoteException;
	public BigDecimal pi_sessionPers() throws RemoteException;
	public BigDecimal pi() throws RemoteException;
	public BigDecimal pi(int digits) throws RemoteException;
}
