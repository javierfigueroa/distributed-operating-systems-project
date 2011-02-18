

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011 
 * University of Florida
 * 
 */
public class Server implements Runnable {

	public Server() throws IOException {
		this.socket = new ServerSocket(0);
		this.host = PropertyManager.getProperties().get("RW.server");
		this.port = this.socket.getLocalPort();
		Log.write("Server has started on port " + this.port + "!");
		Log.write("Host name: " + this.host);
		
		startClients();
	}


	private void startClients() throws IOException {
		int i = 0;
		while (i < numberOfReaders) { // read files with hosts
			String host = PropertyManager.getProperties().get("RW.reader" + Server.workers);
			Executor runner = new Executor(host, this.host, this.port, Server.workers++, Action.read);
			new Thread(runner).start();
			i++;
		}
		
		i = 0;
		while (i < numberOfWriters) { // read files with hosts
			String host = PropertyManager.getProperties().get("RW.writer" + Server.workers);
			Executor runner = new Executor(host, this.host, this.port, Server.workers++, Action.write);
			new Thread(runner).start();
			i++;
		}
	}

	public void run() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		try {
			int limit = (numberOfReaders + numberOfWriters) * numberOfAccesses;
			while (threads.size() < limit) {
				Socket socket = this.socket.accept();

				Log.write("Server: Accepting a new connection...");
				Connection connection = new Connection(socket, Server.request++);
				Thread thread = new Thread(connection);
				threads.add(thread);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		joinThreads(threads);
	}

	private void joinThreads(ArrayList<Thread> threads) {
		// wait for the threads to finish their work
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	

	private String host;
	private int port;
	private ServerSocket socket;
	private int numberOfAccesses = Integer.parseInt(PropertyManager.getProperties().get("RW.numberOfAccesses"));
	private int numberOfReaders = Integer.parseInt(PropertyManager.getProperties().get("RW.numberOfReaders"));
	private int numberOfWriters = Integer.parseInt(PropertyManager.getProperties().get("RW.numberOfWriters"));
	public static int workers = 1;
	
	public static int request = 0;
	public static int service = 0;
	public static SharedObject sharedObject = new SharedObject();
}