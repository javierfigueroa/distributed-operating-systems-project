

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011
 * University of Florida
 *
 */
public class Common {
	public static String NL = System.getProperty("line.separator");
	
	public static boolean connected(String response, String host, int port) {
		if (!response.startsWith("200")) {
			Log.write("Unable to connect to " + host + ":" + port);
			return false;
		}

		return true;
	}

	public static void sendCommand(String command, BufferedWriter writer)
			throws IOException {
		writer.write(command + "\r\n");
		writer.flush();
	}

	public static boolean replied(String response, String host, int port,
			String code) {
		if (!response.startsWith(code)) {
			Log.write("Failed to talk to server " + host + ":" + port
					+ " retrying...");
			return false;
		}

		return true;
	}
}
