package org.openmole.plugin.task.gama;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReceiveMessageInterface extends Remote {
	public String receiveMessage(String x) throws RemoteException;

	public void setVar() throws RemoteException;

	public String startSim(String xmlInput, String folderOut, int numSim)
			throws RemoteException;
}
