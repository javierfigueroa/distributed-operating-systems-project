import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.Random;
/**
 * @author E. Javier Figueroa COP5615 Spring 2011 University of Florida
 * 
 */
public class Multicaster implements Runnable {

	public Multicaster(MulticastSocket socket, int id) throws IOException {
		this.socket = socket;
		this.id = id;
		this.numberOfRequest = Integer.parseInt(PropertyManager.getProperties().get("numberOfRequests"));
		this.multicastPort = Integer.parseInt(PropertyManager.getProperties().get("Multicast.port"));
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {		
		while(true) {

			if (Client.success == numberOfRequest) {
				Log.write("Success: " + Client.success, Client.log);
				Log.write("Leaving multicaster", Client.log);
				break;
			}	
			
			int sequence = Client.sequenceVector.get(this.id);
			if (!Client.hasToken && Client.success == sequence && sequence < numberOfRequest) {
					Client.sequenceVector.put(this.id, sequence + 1);
					String message = "figueroa-" + Client.sequenceVector.get(this.id) + "-" + id;
					DatagramPacket hi = new DatagramPacket(message.getBytes(), message.length(), Client.group, multicastPort);
					try {
						socket.send(hi);
						Log.write("Client" + id+": Broadcasted: " + message, Client.log);
					} catch (IOException e) {
						e.printStackTrace();
					}				
			}	
					
			try {
				Thread.sleep(new Random().nextInt(1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private int multicastPort;
	private int numberOfRequest;
	private MulticastSocket socket;
	private int id;
}
