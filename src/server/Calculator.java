package server;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import client.Client;

public interface Calculator extends java.rmi.Remote {
	public enum Type {
	    NORMAL, CPU, RAM, IO, MIXED
	}
	public BigDecimal pi(Type type,String c) throws RemoteException;
	public BigDecimal pi(int digits,Type type,String c) throws RemoteException;
}
