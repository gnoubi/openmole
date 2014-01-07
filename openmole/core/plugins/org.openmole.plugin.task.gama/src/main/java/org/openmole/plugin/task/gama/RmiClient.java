package org.openmole.plugin.task.gama;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient {
    /*
    static public void main(String args[])
    {
        try {
            RmiClient.callServer();

        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    */

    public static String callServer() {
		ReceiveMessageInterface rmiServer;
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		try {
			// get the �gregistry�h
			registry = LocateRegistry.getRegistry(serverAddress, (new Integer(
					serverPort)).intValue());
			// look up the remote object
			rmiServer = (ReceiveMessageInterface) (registry.lookup("//localhost:3232/rmiServer"));
			// call the remote method
            /*
			System.out.printf("Output of running is: "
					+ rmiServer.startSim("D:\\toto\\predatorPrey.xml",
							"D:\\toto\\tot", 2));
			*/
            return rmiServer.startSim("D:\\toto\\predatorPrey.xml","D:\\toto\\tot", 2);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
        return "fail";
	}
}