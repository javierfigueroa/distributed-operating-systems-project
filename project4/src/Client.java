import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author E. Javier Figueroa COP5615 Spring 2011 University of Florida
 * 
 */
public class Client {

	public Client(int id) throws InterruptedException, IOException {
		this.id = id;
		this.multicastPort = Integer.parseInt(PropertyManager.getProperties().get("Multicast.port"));
		this.multicastAddress = PropertyManager.getProperties().get("Multicast.address");
		this.numberOfRequest = Integer.parseInt(PropertyManager.getProperties().get("numberOfRequests"));

		int clientNumber = Integer.parseInt(PropertyManager.getProperties().get("ClientNum"));
		for(int i=1;i<= clientNumber;i++) {
			sequenceVector.put(i, 0);
			token.getVector().put(i, 0);
		}

		Client.log = "Client" + id;		
		work();
	}

	private void work() throws InterruptedException, NumberFormatException, IOException {

		if (this.id == 1) {
			Log.write("Client 1 setting hasToken");
			Client.hasToken = true;
		}
		
		MulticastSocket multicastSocket = null;
		Thread listenerThread = null;
		Thread multicasterThread = null;
		Thread unicastThread = null;
		
		try {
			Log.write("Starting multicast socket in port: " + multicastPort);
			multicastSocket = new MulticastSocket(multicastPort);
			multicastSocket.setTimeToLive(1);

			Client.group = InetAddress.getByName(multicastAddress);
			multicastSocket.joinGroup(Client.group);
			Listener listener = new Listener(multicastSocket, id);
			listenerThread = new Thread(listener);
			listenerThread.start();
			
			Multicaster multicaster = new Multicaster(multicastSocket, id);
			multicasterThread = new Thread(multicaster);
			multicasterThread.start();
			
			unicastThread = new Thread(new Unicaster(this.id));
			unicastThread.start();				
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while(true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
			
			if (Client.hasToken) {
				dequeue(this.id);
			}

			if (!listenerThread.isAlive()){
				break;
			}
			
			int status = 0;
			for (Integer i : Client.sequenceVector.keySet()) {
				if (Client.sequenceVector.get(i) == numberOfRequest && Client.token.getVector().get(i) == numberOfRequest) {
					status++;
				}
			}
			
			if (status == Client.sequenceVector.size() && Client.token.getQueue().isEmpty() && Client.hasToken) {
				Log.output(this.id, Client.sequenceVector.values().toString(), Client.token.getVector().values().toString(), Client.token.getQueue().toString());
				break;
			}
		}

		Log.write("Out of the loop...");

		String message = "figueroa-0-0";
		DatagramPacket hi = new DatagramPacket(message.getBytes(), message.length(), Client.group, multicastPort);
		try {
			multicastSocket.send(hi);
			Log.write("Client" + id+": Broadcasted: " + message, Client.log);
		} catch (IOException e) {
			e.printStackTrace();
		}			
		
		listenerThread.join();
		unicastThread.interrupt();
		multicastSocket.close();
		
		Log.write("Exiting...");
		System.exit(0);
	}

	public void dequeue(int memberId){
		if (!Client.token.getQueue().isEmpty()) {
			int head = Client.token.getQueue().peek();

			if (Client.sequenceVector.get(head) == Client.token.getVector().get(head) + 1) {
				Log.output(memberId, Client.sequenceVector.values().toString(), Client.token.getVector().values().toString(), Client.token.getQueue().toString());
				Log.write("dequeing... queue: " + Client.token.getQueue().toString(), Client.log);
				int id = Client.token.getQueue().poll();
				sendToken(id);
			}
		}
	}
	
	public void sendToken(int id) {
		String host = PropertyManager.getProperties().get("Client"+id);
		int port = Integer.parseInt(PropertyManager.getProperties().get("Client"+id+".port"));

		Log.write("Sending token to clientid: " + id + " host: " + host + " port: " + port + " vector " + Client.token.getVector().toString().replace(" ", "") +
				" queue " + Client.token.getQueue().toString().replaceAll(" ", ""), Client.log);

		Socket socket = null;
		try {
			socket = new Socket(host, port);
			PrintWriter socketWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
			BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String readLine = socketReader.readLine();
			Log.write("Received " + readLine, Client.log);
			Client.hasToken = false; 
			socketWriter.println("token " + Client.token.getVector().toString().replaceAll(" ", "") + " " + Client.token.getQueue().toString().replaceAll(" ", ""));

			Client.token.getQueue().clear();
			readLine = socketReader.readLine();
			Log.write("Received " + readLine, Client.log);
			socketWriter.println("quit");
			readLine = socketReader.readLine();
			Log.write("Closing socket", Client.log);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private int id;
	private int multicastPort;
	private String multicastAddress;
	private int numberOfRequest;
	
	public static String log;
	public static Boolean hasToken = false;
	public static int success = 0;
	public static InetAddress group;
	public static Token token = new Token();
	public static volatile Map<Integer, Integer> sequenceVector = new HashMap<Integer, Integer>();
}
