import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
			Registry registry = LocateRegistry.getRegistry(host, port);
            Connection stub = (Connection) registry.lookup("figueroa");
            String response = null;
            if (action == Action.read) {
	            response = stub.read(this.id);
			} else {
	            response = stub.write(this.id);
			}

			Log.writeToFile(response, log);
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

	private String host;
	private int port;
	private int id;
	private Action action;
	private int times;
	private long sleep;
	private String log;
	private Socket socket;
}
