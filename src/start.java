

import java.io.IOException;

public class start {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 0 && args.length != 5 && args.length > 5) {
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
				Action type = Action.valueOf(args[3]);
				int times = Integer.parseInt(args[4]);
				
				Log.writeToFile("Processing Command Line Arguments: ", id);
				Log.writeToFile("Host: " + host, id);
				Log.writeToFile("Port: " + port, id);
				Log.writeToFile("ClientID: " + id, id);
				Log.writeToFile("Action type: " + type.name(), id);
				Log.writeToFile("Number of Accesses: " + times, id);
				Log.writeToFile("---------------", id);
				new Client(host, port, id, type, times);
			} 
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}

}
