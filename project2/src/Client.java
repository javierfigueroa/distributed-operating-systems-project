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

	public Client(String host, int port, int id, Action action, int times,
			long sleep) {
		this.host = host;
		this.port = port;
		this.id = id;
		this.action = action;
		this.times = times;
		this.log = (action == Action.read ? "R" : "W") + id;

		initLogFile(host, port, id, action, times, sleep);

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
		for (int i = 0; i < times; i++) {
			// connect to server
			if (!connect()) {
				String error = String.format(
						"Failed to connect to server => %s:%d", this.host,
						this.port);
				Log.write(error);
				throw new Exception(error);
			}

			// send message to server
			Common.sendCommand(String.format("%s %d", action.name(), this.id),
					this.writer);

			String response = this.reader.readLine();
			if (!Common.replied(response, this.host, this.port, "226")) {
				String error = String.format(
						"Failed to send action to server => %s:%d", this.host,
						this.port);
				Log.write(error);
				throw new Exception(error);
			}

			// get server response
			StringTokenizer st = new StringTokenizer(response);
			st.nextToken();
			int request = Integer.parseInt(st.nextToken());
			int service = Integer.parseInt(st.nextToken());

			int value = 0;
			if (st.hasMoreTokens()) {
				value = Integer.parseInt(st.nextToken());
			}

			if (action == Action.read) {
				Log.writeToFile(request + "\t\t\t\t" + service + "\t\t\t\t" + value, log);
			} else {
				Log.writeToFile(request + "\t\t\t\t" + service, log);
			}

			// terminate
			Common.sendCommand("BYE", this.writer);
			this.reader.readLine();
			Thread.sleep(sleep);
		}
	}

	private void initLogFile(String host, int port, int id, Action action,
			int times, long sleep) {
		Log.writeToFile("Processing Command Line Arguments: ", log);
		Log.writeToFile("Host: " + host, log);
		Log.writeToFile("Port: " + port, log);
		Log.writeToFile("Number of Accesses: " + times, log);
		Log.writeToFile("Sleep Time: " + sleep, log);
		Log.writeToFile("Client Type: " + action.name()
				+ (action == Action.read ? "er" : "r"), log);
		Log.writeToFile("Client Name: " + id, log);
		if (action == Action.read) {
			Log.writeToFile("Request Sequence	Service Sequence \t	Object Value",
					log);
			Log.writeToFile("----------------	---------------- \t	------------",
					log);
		} else {
			Log.writeToFile("Request Sequence	Service Sequence", log);
			Log.writeToFile("----------------	----------------", log);
		}
	}

	private boolean connect() throws InterruptedException {
		int attempt = 0;
		while (attempt < 5) {
			attempt++;
			try {
				this.socket = new Socket(this.host, this.port);

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

	private String host;
	private int port;
	private int id;
	private Action action;
	private int times;
	private long sleep;
	private String log;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;
}
