

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
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
	private int id;

	public Socket getSocket() {
		return this.socket;
	}

	public Connection(Socket socket, int id) throws IOException {
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		this.writer = new PrintWriter(new OutputStreamWriter(socket
				.getOutputStream()), true);
		this.id = id;
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

			Log.write("Received command " + command, Client.log);
			int code = 0;
			if (command.equals("token")) {
				code = read(st);
			} else if (command.equals("quit")) {
				code = quit();
			}

			if (code == 221 || code == 0)
				return;
		}
	}

	private int read(StringTokenizer st) throws InterruptedException {
		String vector = st.nextToken();
		String queue = st.nextToken();		

		Client.success++;
		// update T[i] = S[i] and Q
		Client.token.updateToken(this.id, vector, queue);
		// compare Si[K] = Ti[K] + 1 => k != i and add to queue if not in queue
		for (int i = Client.sequenceVector.size(); i > 0 ; i--) {
			if (i != this.id && Client.sequenceVector.get(i) == Client.token.getVector().get(i) + 1 && !Client.token.getQueue().contains(i)) {
				Client.token.getQueue().add(i);
			}
		}
		
		Client.hasToken = true;
		Log.write("I HAVE THE TOKEN! CLIENT => " + this.id,	Client.log);
		Log.write("Received vector: " + vector + " and queue: " + queue , Client.log);
		Log.write("Queue now is: " + Client.token.getQueue().toString());
		Log.write("Vector now is: " + Client.token.getVector().toString());
		Thread.sleep(new Random().nextInt(1000));
		return reply(226);
	}

	private int quit() throws IOException {
		return reply(221);
	}

	private int reply(int code) {
		this.writer.println(code);
		return code;
	}
}