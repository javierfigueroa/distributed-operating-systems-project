

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011 
 * University of Florida
 * 
 */
public class start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 0 && args.length != 6 && args.length > 6) {
			Log.write("Usage: start.jar");
			System.exit(1);
		}
		
		try {
			if (args.length == 0) {
				// server start
				Log.write("Starting Server thread...");
				String host = PropertyManager.getProperties().get("RW.server");
				int port = Integer.parseInt(PropertyManager.getProperties().get("Rmiregistry.port"));
				Server server = new Server(host, port);
				Connection stub = (Connection) UnicastRemoteObject.exportObject(server, 0);
	            Registry registry = LocateRegistry.getRegistry(host, port);
	            registry.bind("figueroa", stub);
	            
				Thread thread = new Thread(server);
				thread.start();
				thread.join();
			} else if (args.length == 6) {
				// client start
				String host = args[0];
				int port = Integer.parseInt(args[1]);
				int id = Integer.parseInt(args[2]);
				Action type = Action.valueOf(args[3]);
				int times = Integer.parseInt(args[4]);
				long sleep = Long.parseLong(args[5]);
				new Client(host, port, id, type, times, sleep);
			} 
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (AlreadyBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(0);
	}

}
