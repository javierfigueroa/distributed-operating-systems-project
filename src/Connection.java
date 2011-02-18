

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * @author E. Javier Figueroa 11/27/2010 CNT 5106c Fall 2010 University of
 *         Florida
 * 
 */
public class Connection implements Runnable {

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private int request;

	public Socket getSocket() {
		return this.socket;
	}

	public Connection(Socket socket, int request) throws IOException {
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		this.writer = new PrintWriter(new OutputStreamWriter(socket
				.getOutputStream()), true);
		this.request = request;
	}

	public void run() {
		try {
			listen();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				this.socket.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		this.socket.close();
	}

	/**
	 * Listens for commands coming in through the connection socket
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void listen() throws IOException, InterruptedException {
		reply(200);

		String line = null;
		while ((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			String command = st.nextToken().toLowerCase();

			int code = 0;
			if (command.equals("write")) {
				code = write(st);
			} else if (command.equals("read")) {
				code = read(st);
			} else if (command.equals("quit")) {
				code = quit();
			}

			if (code == 221 || code == 0)
				return;
		}
	}

	private int read(StringTokenizer st) throws InterruptedException {
		int clientId = Integer.parseInt(st.nextToken());
		long opTime = Long.parseLong(PropertyManager.getProperties().get("RW.reader"+clientId+".opTime"));
		Log.write(Thread.currentThread().getName() + "(Reader" + clientId + "): Queueing ClientID" + clientId + " ...");
		Server.readers++;

		int value = Server.sharedObject.getValue();
		int service = Server.service;
		int readers = Server.readers;
		
		Server.readerOutput.append(service + "\t\t\t\t" + value + "\t\t\t\t" + "R" + clientId + "\t\t\t\t" + readers + Common.NL);
		
		synchronized (this) {
			wait(opTime);
		}
		
		Server.readers--;
		return reply(226, request + " " + service + " " + String.valueOf(value));
	}

	private int write(StringTokenizer st) throws InterruptedException {
		int clientId = Integer.parseInt(st.nextToken());
		long opTime = Long.parseLong(PropertyManager.getProperties().get("RW.writer"+clientId+".opTime"));
		Log.write(Thread.currentThread().getName() + "(Writer " + clientId + "): Queueing ClientID" + clientId + " ...");

		Server.sharedObject.setValue(clientId);
		int service = Server.service;

		Server.writerOutput.append(service + "\t\t\t\t" + clientId + "\t\t\t\t" + "W" + clientId + Common.NL);

		synchronized (this) {
			wait(opTime);
		}
		
		return reply(226, request + " " + service);
	}

	private int quit() throws IOException {
		this.socket.close();
		return reply(221);
	}

	public int reply(int code) {
		this.writer.println(code);
		return code;
	}

	public int reply(int code, String message) {
		this.writer.println(code + " " + message);
		return code;
	}

}