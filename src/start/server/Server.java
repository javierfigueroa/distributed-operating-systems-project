package start.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import start.Common;
import start.Log;

public class Server implements Runnable {
	private String host;
	private int port;
	private ServerSocket socket;
	private int workers = 0;

	public static HashMap<Integer, Transaction> numbers = new HashMap<Integer, Transaction>();
	public static HashMap<String, Integer> rating = new HashMap<String, Integer>(); // host and CPU rating (1-10)
	public static ArrayList<Integer> result = new ArrayList<Integer>();
	
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
			Exec runner = new Exec(host, this.host, this.port, this.workers++);
			new Thread(runner).start(); 
		}
	}

	public void run() {
		ArrayList<Thread> threads = new ArrayList<Thread>();

		try {
			while (threads.size() < this.workers) {
				Socket socket = this.socket.accept();
				
				Server.rating.put(socket.getInetAddress().getHostName() + ":" + socket.getPort(), -1);
				
				Log.write("Server: Accepting a new connection...");
				Connection connection = new Connection(socket);
				Thread thread = new Thread(connection);
				threads.add(thread);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// wait for the threads to finish their work
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// merge the result
		Log.write("Server: Received all sorted subseq");
		Integer[] sorted = new Integer[Server.result.size()];
		sorted = Server.result.toArray(sorted);
		Log.write("Server: Doing merge sort...");
		MergeSort sorter = new MergeSort();
		sorter.sort(sorted);
		
		Log.write("-------------------------------------");
		Log.write("Server: Final Sorted Sequence");
		for ( Integer number : sorter.getNumbers()) {
			System.out.print(number + " ");
		}
		System.out.println();
		Log.write("Server: Exiting...");
	}
}
