

import java.io.IOException;


/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011
 * University of Florida
 *
 */
public class start {
	public static void main(String[] args) {

		if (args.length != 0 && args.length != 3 && args.length > 3) {
			Log.write("Usage: start.jar");
			System.exit(1);
		}
		
		try {
			if (args.length == 0) {
				// server start
				Log.write("Starting Server thread...");
				Server server = new Server();
				Thread thread = new Thread(server);
				
				thread.start();
				thread.join();
			} else if (args.length == 3) {
				// client start
				String host = args[0];
				int port = Integer.parseInt(args[1]);
				int id = Integer.parseInt(args[2]);
				
				Log.writeToFile("Processing Command Line Arguments: ", id);
				Log.writeToFile("Host: " + host, id);
				Log.writeToFile("Port: " + port, id);
				Log.writeToFile("ClientID: " + id, id);
				Log.writeToFile("---------------", id);
				new Client(host, port, id);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.exit(0);
	}
}
