

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011 
 * University of Florida
 * 
 */
public class Server implements Runnable {
	
	public Server() throws IOException, InterruptedException {
		this.socket = new ServerSocket(0);
		this.host = PropertyManager.getProperties().get("RW.server");
		this.port = this.socket.getLocalPort();
		Log.write("Server has started on port " + this.port + "!");
		Log.write("Host name: " + this.host);
		
		startClients();
		initOutput();
	}

	private void initOutput() {
		readerOutput.append("Read Requests:").append(Common.NL);
		readerOutput.append("Service Sequence \t\tObject Value \t\t Read by   \t\t	 Num of Readers").append(Common.NL);
		readerOutput.append("---------------- \t\t------------ \t\t --------- \t\t	 --------------").append(Common.NL);
		
		writerOutput.append("Write Requests:").append(Common.NL);
		writerOutput.append("Service Sequence \t\tObject Value \t\t Written by").append(Common.NL);
		writerOutput.append("---------------- \t\t------------ \t\t ----------").append(Common.NL);		
	}

	private void startClients() throws IOException, InterruptedException {
		Random randomSleep = new Random();
		
		int i = 0;
		while (i < numberOfReaders) { // read files with hosts
			String host = PropertyManager.getProperties().get("RW.reader" + workers);
			long sleep = Long.parseLong(PropertyManager.getProperties().get("RW.reader"+workers+".sleepTime"));
			Thread.sleep(500 + randomSleep.nextInt(500));
			Executor runner = new Executor(host, this.host, this.port, workers++, Action.read, sleep);
			new Thread(runner).start();
			i++;
		}
		
		i = 0;
		while (i < numberOfWriters) { // read files with hosts
			String host = PropertyManager.getProperties().get("RW.writer" + workers);
			long sleep = Long.parseLong(PropertyManager.getProperties().get("RW.writer"+workers+".sleepTime"));
			Thread.sleep(500 + randomSleep.nextInt(500));
			Executor runner = new Executor(host, this.host, this.port, workers++, Action.write, sleep);
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
				Connection connection = new Connection(socket, ++Server.request);
				Thread thread = new Thread(connection);
				threads.add(thread);
				thread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		joinThreads(threads);
		Log.write(Server.readerOutput.toString());
		Log.write(Server.writerOutput.toString());
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
	private int workers = 1;
	private int numberOfAccesses = Integer.parseInt(PropertyManager.getProperties().get("RW.numberOfAccesses"));
	private int numberOfReaders = Integer.parseInt(PropertyManager.getProperties().get("RW.numberOfReaders"));
	private int numberOfWriters = Integer.parseInt(PropertyManager.getProperties().get("RW.numberOfWriters"));
	
	public static int request = 0;
	public static volatile int service = 0;

	public static SharedObject sharedObject = new SharedObject();
	public static volatile StringBuffer readerOutput = new StringBuffer();
	public static volatile StringBuffer writerOutput = new StringBuffer();
}