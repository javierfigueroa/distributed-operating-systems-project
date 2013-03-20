

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Random;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011 
 * University of Florida
 * 
 */
public class Server implements Connection, Runnable{
	
	public Server(String host, int port) throws IOException, InterruptedException {
		this.host = host;
		this.port = port;
		Log.write("Server has started on port " + this.port + "!");
		Log.write("Host name: " + this.host);
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

	public String read(int clientId) throws RemoteException, InterruptedException {
		Server.request++;
		long opTime = Long.parseLong(PropertyManager.getProperties().get("RW.reader"+clientId+".opTime"));
		Log.write(Thread.currentThread().getName() + "(Reader " + clientId + "): Processing...");
		int value = Server.sharedObject.getValue(clientId, opTime);
		return request + "\t\t\t\t" + Server.service + "\t\t\t\t" + String.valueOf(value);
	}

	public String write(int clientId) throws InterruptedException, RemoteException {
		Server.request++;
		long opTime = Long.parseLong(PropertyManager.getProperties().get("RW.writer"+clientId+".opTime"));
		Log.write(Thread.currentThread().getName() + "(Writer " + clientId + "): Processing...");
		Server.sharedObject.setValue(clientId, opTime);
		return request + "\t\t\t\t" + Server.service;
	}
	
	public void run() {		
		try {
			startClients();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		while(pending()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		exit();
	}

	public Boolean pending() {
		return Server.request < (numberOfReaders + numberOfWriters) * numberOfAccesses;
	}

	private void exit() {
		Log.write(Server.readerOutput.toString());
		Log.write(Server.writerOutput.toString());
		System.exit(0);
	}

	private String host;
	private int port;
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