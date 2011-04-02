

import java.io.IOException;
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
		if (args.length != 0 && args.length != 1 && args.length > 1) {
			Log.write("Usage: start.jar");
			System.exit(1);
		}
		
		try {
			if (args.length == 0) {
				// server start
				Log.write("Starting Server thread...");
				new Server();
			} else if (args.length == 1) {
				// client start
				int id = Integer.parseInt(args[0]);
				new Client(id);
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
