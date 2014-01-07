package org.openmole.plugin.task.gama;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServer extends java.rmi.server.UnicastRemoteObject implements
		ReceiveMessageInterface {
	int thisPort;
	String thisAddress;
	Registry registry; // rmi registry for lookup the remote objects.
	// This method is called from the remote client by the RMI.
	// This is the implementation of the �gReceiveMessageInterface�h.
	@Override
	public String receiveMessage(String x) throws RemoteException
	{
		System.out.println(x);
		return "ok";
	}

	public RmiServer() throws RemoteException
	{
		try {
			// get the address of this host.
			thisAddress = (InetAddress.getLocalHost()).toString();
		}
		catch (Exception e) {
			throw new RemoteException("can't get inet address.");
		}
		thisPort = 3232; // this port(registry�fs port)
		System.out.println("this address=" + thisAddress + ",port=" + thisPort);
		try {
			// create the registry and bind the name and object.
			registry = LocateRegistry.createRegistry(thisPort);
			registry.rebind("//localhost:3232/rmiServer", this);
		}
		catch (RemoteException e) {
			throw e;
		}
	}

	static public void main(String args[])
	{
		try {
			new RmiServer();

		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public String startSim(final String xmlInput, final String folderOut,
			final int numSim) throws RemoteException {
		// TODO Auto-generated method stub
		String res = "";
		try {
			ProcessBuilder pb = new ProcessBuilder(
					new String[] {
							"\"C:\\Program Files\\Java\\jre6\\bin\\javaw.exe\"",
							"-Xms400m",
							"-Xmx5120m",
							"-jar",
							"\"D:\\GAMAbin\\eclipse\\plugins\\org.eclipse.equinox.launcher_1.2.0.v20110502.jar\"",
							"-application", "msi.gama.headless.id4",
							"\"" + xmlInput + "\"", "\"" + folderOut + "\"",
							"" + numSim });
			pb.redirectErrorStream(true);
			Process process = pb.start();

			// Process process = new ProcessBuilder(
			// "C:\\Users\\hqnghi\\Desktop\\GAMA\\a.bat").start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			// System.out.printf("Output of running is:");
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				res += line + "\r";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// System.exit(0);
		return res;
	}

	@Override
	public void setVar() throws RemoteException {
		// TODO Auto-generated method stub

	}
}
