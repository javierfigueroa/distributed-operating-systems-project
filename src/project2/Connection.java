package project2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author E. Javier Figueroa 11/27/2010 CNT 5106c Fall 2010 University of
 *         Florida
 * 
 */
public class Connection implements Runnable {

	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;

	public Socket getSocket() {
		return this.socket;
	}

	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		this.writer = new PrintWriter(new OutputStreamWriter(socket
				.getOutputStream()), true);
	}

	@Override
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
			} else if (command.equals("port")) {
				code = setPort(st);
			} else if (command.equals("quit")) {
				code = quit();
			}

			if (code == 221 || code == 0)
				return;
		}
	}

	private int read(StringTokenizer st) throws InterruptedException {
		int clientId = Integer.parseInt(st.nextToken());
		long opTime = Long.parseLong(st.nextToken());
		Read read = new Read(clientId);
		read.setWriter(writer);
//		Server.sharedObject.queue(read);
		
		Log.write(Thread.currentThread().getName() + "( Reader" + read.getId() + "): Queueing ClientID" + read.getId() + " ...");
		synchronized (this) {
			wait(opTime);
		}
		
		int value = Server.sharedObject.getValue();
		
		return reply(226, String.valueOf(value));
	}

	private int write(StringTokenizer st) throws InterruptedException {
		int clientId = Integer.parseInt(st.nextToken());
		long opTime = Long.parseLong(st.nextToken());
		int value = Integer.parseInt(st.nextToken());
		
		Write write = new Write(clientId, value);
		write.setWriter(writer);
//		Server.sharedObject.queue(write);
		
		Log.write(Thread.currentThread().getName() + "( Writer " + write.getId() + "): Queueing ClientID" + write.getId() + " ...");
		synchronized (this) {
			wait(opTime);
		}
		
		Server.sharedObject.setValue(value);
		
		return reply(226);
	}

	/**
	 * Sets the port to be used in an opened connection
	 * 
	 * @param st
	 *            command
	 * @return 200 to acknowledge the setting of the port
	 */
	private int setPort(StringTokenizer st) {
		String portStr = st.nextToken();
		st = new StringTokenizer(portStr, ",");
		String h1 = st.nextToken();
		String h2 = st.nextToken();
		String h3 = st.nextToken();
		String h4 = st.nextToken();
		int p1 = Integer.parseInt(st.nextToken());
		int p2 = Integer.parseInt(st.nextToken());

		String dataHost = h1 + "." + h2 + "." + h3 + "." + h4;
		int dataPort = (p1 << 8) | p2;

		// this.socketMessenger.setDataPort(dataHost, dataPort);

		return reply(200);
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