package project2;

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
	private String host;
	private int port;
	private ServerSocket socket;
	private static int workers = 1;

	public Server() throws IOException {
		this.socket = new ServerSocket(0);
		this.host = PropertyManager.getProperties().get("RW.server");
		this.port = this.socket.getLocalPort();
		Log.write("Server has started on port " + this.port + "!");
		Log.write("Host name: " + this.host);
		
		startClients();
	}


	private void startClients() throws IOException {
		while (Server.workers < 9) { // read files with hosts
			String host = PropertyManager.getProperties().get("RW.reader" + Server.workers);
			Executor runner = new Executor(host, this.host, this.port, Server.workers++, ClientType.reader);
			new Thread(runner).start();
		}
		
		Server.workers = 1;
		while (Server.workers < 9) { // read files with hosts
			String host = PropertyManager.getProperties().get("RW.writer" + Server.workers);
			Executor runner = new Executor(host, this.host, this.port, Server.workers++, ClientType.writer);
			new Thread(runner).start();
		}
	}

	public void run() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<Connection> connections = new ArrayList<Connection>();
		try {
			while (threads.size() < Server.workers - 1) {
				Socket socket = this.socket.accept();

				Log.write("Server: Accepting a new connection...");
				Connection connection = new Connection(socket);
				Thread thread = new Thread(connection);
				connections.add(connection);
				threads.add(thread);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		wakeThreads(connections);
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

	private void wakeThreads(ArrayList<Connection> connections) {
		// Wake up connection threads
		for (Connection connection : connections) {
			synchronized (connection) {
				connection.notify();
			}
		}
	}
}