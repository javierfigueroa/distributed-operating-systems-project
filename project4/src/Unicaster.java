import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * @author E. Javier Figueroa COP5615 Spring 2011 University of Florida
 * 
 */
public class Unicaster implements Runnable {

	public Unicaster(int id) throws NumberFormatException, IOException {
		this.socket = new ServerSocket(Integer.parseInt(PropertyManager.getProperties().get("Client"+id+".port")));
		this.host = PropertyManager.getProperties().get("Client"+id);
		this.id = id;
		this.numberOfRequest = Integer.parseInt(PropertyManager.getProperties().get("numberOfRequests"));
		
		Log.write("Client has started on port " + this.socket.getLocalPort() + "!");
		Log.write("Host name: " + this.host);		
		initLogFile(this.socket.getLocalPort(), id);
	}
	
	public void run() {
		try {			
			while(true){
				Socket socket = this.socket.accept();
				Log.write("Server: Accepting a new connection...");
				Connection connection = new Connection(socket, this.id);
				Thread thread = new Thread(connection);
				thread.start();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	private void initLogFile(int port, int id) {
		Log.write("Processing Command Line Arguments: ", Client.log);
		Log.write("Port: " + port, Client.log);
		Log.write("Number of Accesses: " + numberOfRequest, Client.log);
		Log.write("Client Id: " + id, Client.log);
	}
	
	private int id;
	private String host;
	private int numberOfRequest;
	private ServerSocket socket;
}
