package project2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * @author E. Javier Figueroa COP5615 Spring 2011 University of Florida
 * 
 */
public class Client {
	private String host;
	private int port;
	private int id;
	private Action action;
	private int times;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;

	public Client(String host, int port, int id, Action action, int times) {
		this.host = host;
		this.port = port;
		this.id = id;
		this.action = action;
		this.times = times;

		try {
			work();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}

	@Override
	public void finalize() {
		try {
			this.socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void work() throws Exception {
		// connect to server
		
		Log.writeToFile("Client Type: " + action.name() + "r", id);
		Log.writeToFile("Client Name: " + id, id);
		if (action == Action.read) {
			Log.writeToFile("Request Sequence	Service Sequence	Object Value", id);
			Log.writeToFile("----------------	----------------	------------", id);
		}else{
			Log.writeToFile("Request Sequence	Service Sequence", id);
			Log.writeToFile("----------------	----------------", id);
		}
		
		if (!connect()) {
			String error = String.format(
					"Failed to connect to server => %s:%d", this.host,
					this.port);
			Log.write(error);
			throw new Exception(error);
		}

		for (int i = 0; i < times; i++) {
			Common.sendCommand(String.format("%s %d", action.name(), this.id),
					this.writer);
			Log.writeToFile("Sent: action " + action.name(), this.id);

			String response = this.reader.readLine();
			if (!Common.replied(response, this.host, this.port, "226")) {
				String error = String.format(
						"Failed to send action to server => %s:%d", this.host,
						this.port);
				Log.write(error);
				throw new Exception(error);
			}

			// get numbers to sort from server
			StringTokenizer st = new StringTokenizer(response);
			st.nextToken();
			int request = Integer.parseInt(st.nextToken());
			int service = Integer.parseInt(st.nextToken());
			
			int value = 0;
			if (st.hasMoreTokens()) {
				value = Integer.parseInt(st.nextToken());
			}
			
			if (action == Action.read) {
				Log.writeToFile(request+"	"+service+"	"+value, id);
			}else{
				Log.writeToFile(request+"	"+service, id);
			}
		}

		// terminate
		Common.sendCommand("BYE", this.writer);
		Log.writeToFile("BYE", this.id);
		this.reader.readLine();
		Log.writeToFile("Terminating Connection! OK!", this.id);
	}

	private boolean connect() throws InterruptedException {
		int attempt = 0;
		while (attempt < 5) {
			attempt++;
			try {
				this.socket = new Socket(this.host, this.port);
				Log
						.writeToFile("Starting remote connection: SUCCESS!",
								this.id);

				this.reader = new BufferedReader(new InputStreamReader(
						this.socket.getInputStream()));
				this.writer = new BufferedWriter(new OutputStreamWriter(
						this.socket.getOutputStream()));

				String response = this.reader.readLine();
				if (Common.connected(response, this.host, this.port))
					return true;
			} catch (Exception e) {
				Log
						.write("Failed connection to the server. Attempt connection : "
								+ attempt);
			}

			Thread.sleep(1000);
		}

		return false;
	}
}
