import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
/**
 * @author E. Javier Figueroa COP5615 Spring 2011 University of Florida
 * 
 */
public class Listener implements Runnable {

	public Listener(MulticastSocket socket, int id) throws IOException {
		this.socket = socket;
		this.id = id;
	}

	public void run() {
		while (true) {
			byte[] buf = new byte[1000];
			DatagramPacket recv = new DatagramPacket(buf, buf.length);
			try {
				socket.receive(recv);
			} catch (IOException e) {
				break;
			}

			String broadcast = new String(recv.getData(), 0, recv.getLength());
			if (broadcast.startsWith("figueroa")) {
				String[] message = broadcast.split("-");
				int sequence = Integer.parseInt(message[1]);
				int clientId = Integer.parseInt(message[2]);

				Log.write("Client" + id + ": received broadcast sequence "+sequence + " clientId " + clientId, Client.log);
				if (sequence == 0 && clientId == 0) {
					break;
				}

				Client.sequenceVector.put(clientId, Math.max(Client.sequenceVector.get(clientId), sequence));
				if (clientId != this.id && Client.hasToken && !Client.token.getQueue().contains(clientId)) {
						Log.write("Client" + id + ": queueing client"+clientId, Client.log);
						Client.token.getQueue().add(clientId);
				}				
			}			
		}
	}	

	private int id;
	private MulticastSocket socket;
}
