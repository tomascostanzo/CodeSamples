package ca.polymtl.inf4410.tp2.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import ca.polymtl.inf4410.tp2.shared.Pair;

public interface ServerInterface extends Remote {
	String test() throws RemoteException;
	ArrayList<Integer> calculer(ArrayList<Pair> operations) throws RemoteException;
	void setQ(int q) throws RemoteException;
	void setM(int m) throws RemoteException;
}
