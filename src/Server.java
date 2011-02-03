

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


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
	private static HashMap<Integer, Transaction> numbers = new HashMap<Integer, Transaction>();
	private static HashMap<String, Integer> rating = new HashMap<String, Integer>(); 
	private static ArrayList<Integer> result = new ArrayList<Integer>();

	public static synchronized void decreaseWorkers() {
		Server.workers--;
	}

	public static synchronized int getWorkers() {
		return Server.workers;
	}

	public static synchronized void addRating(String host, Integer rating) {
		Server.rating.put(host, rating);
	}

	public static synchronized HashMap<String, Integer> getRatings() {
		return Server.rating;
	}

	public static synchronized void addNumbers(Integer number,
			Transaction transaction) {
		Server.numbers.put(number, transaction);
	}

	public static synchronized HashMap<Integer, Transaction> getNumbers() {
		return Server.numbers;
	}

	public static synchronized void addResult(Integer number) {
		if (!Server.getResult().contains(number)) {
			Server.result.add(number);
		}
	}

	public static synchronized ArrayList<Integer> getResult() {
		return Server.result;
	}

	public Server() throws IOException {
		this.socket = new ServerSocket(0);
		this.host = InetAddress.getLocalHost().getHostName();
		this.port = this.socket.getLocalPort();
		Log.write("Server has started on port " + this.port + "!");
		Log.write("Host name: " + this.host);

		generateNumbers();
		startClients();
	}

	private void generateNumbers() {
		while (Server.numbers.size() < 500) {
			int number = Common.random(1, 500);
			if (!Server.numbers.containsKey(number)) {
				Server.numbers.put(number, new Transaction());
			}
		}
	}

	private void startClients() throws IOException {
		for (String host : Common.MACHINES) { // read files with hosts
			Exec runner = new Exec(host, this.host, this.port, Server.workers++);
			new Thread(runner).start();
		}
	}

	public void run() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<Connection> connections = new ArrayList<Connection>();
		try {
			while (threads.size() < Server.workers - 1) {
				Socket socket = this.socket.accept();
				Server.rating.put(socket.getInetAddress().getHostName() + ":"
						+ socket.getPort(), -1);
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

		// Wait for the ratings to come back
		while (Server.getRatings().containsValue(-1)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Wake up connection threads
		for (Connection connection : connections) {
			synchronized (connection) {
				connection.notify();
			}
		}

		// wait for the threads to finish their work
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// merge the result
		Log.write("Server: Received all sorted subseq");
		int[] sorted = new int[Server.result.size()];
		for (int i=0;i<sorted.length;i++) {
			sorted[i] = Server.result.get(i);
		}
		
		Log.write("Server: Doing merge sort...");
		MergeSort sorter = new MergeSort(sorted);
		sorter.sort();
		
		Log.write("-------------------------------------");
		Log.write("Server: Final Sorted Sequence");
		for (int number : sorter.getList()) {
			System.out.print(number + " ");
		}
		System.out.println();
		Log.write("Server: Exiting...");
	}
}
