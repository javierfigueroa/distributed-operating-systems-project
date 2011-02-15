package project1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011
 * University of Florida
 *
 */
public class Connection implements Runnable {
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private int clientId;

	public Socket getSocket() {
		return this.socket;
	}

	public Connection(Socket socket) throws IOException {
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
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

	private void listen() throws IOException, InterruptedException {
		reply(200);

		String line = null;
		while ((line = reader.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(line);
			String command = st.nextToken().toLowerCase();

			int code = 0;
			if (command.equals("cpu")) {
				code = register(st);
			} else if (command.equals("numbers")) {
				code = save(st);
			} else if (command.equals("bye")) {
				code = quit();
			}

			if (code == 221 || code == 0) return;
		}
		
	}
	
	private int quit() throws IOException {
		this.socket.close();
		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Terminates successfully! OK!");
		return reply(221);
	}

	private int save(StringTokenizer st) {
		String content = st.nextToken();
		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Received sorted subseq from Client ID" + this.clientId + " " + content);
		int numbers[] = Common.stringToArray(content);
		
		for (Integer number : numbers) {
			Server.addResult(number);
		}
		
		return reply(226);
	}

	private int register(StringTokenizer st) throws InterruptedException {
		String content = st.nextToken();
		int rating = Integer.parseInt(content.split(",")[0]);
		this.clientId = Integer.parseInt(content.split(",")[1]);
		Transaction transaction = setRating(rating);
		
		//wait until all cpu ratings are updated
		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Waiting for other threads before assigning numbers to ClientID" + this.clientId + " ...");
		while (Server.getRatings().size() < Server.getWorkers() - 1) {
			synchronized (this) {
				wait();
			}
		}
		
		Thread.sleep(1000);
		// calculate amount of numbers to send
		int ratio = getRatio(rating);
		// compose list of numbers for client
		String numbers = getNumbers(transaction, ratio);		
		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Assigning " + numbers + " to ClientID" + this.clientId);
		return reply(226, numbers);
	}

	private String getNumbers(Transaction transaction, int ratio) {
		StringBuilder builder = new StringBuilder();
		int count = 0;
		HashMap<Integer, Transaction> numbers = Server.getNumbers();
		for (Integer number : numbers.keySet()) {
			if (!numbers.get(number).isSorted()) {
				builder.append(String.valueOf(number));
				count++;
				transaction.setSorted(true);
				Server.getNumbers().put(number, transaction);
				
				if (count == ratio) break;
				builder.append(",");
			}
		}
		return builder.toString();
	}

	private int getRatio(int rating) {
		int cumulativeCPUspeed = 0;
		for (Integer speed : Server.getRatings().values()) {
			cumulativeCPUspeed += speed;
		}
		
		int ratio = (int) ((rating / (double)cumulativeCPUspeed) * Server.getNumbers().size());
		return ratio;
	}

	private Transaction setRating(int rating) {
		Server.addRating(this.socket.getInetAddress().getHostName() + ":" + socket.getPort(), rating);
		Transaction transaction = new Transaction();
		transaction.setClientId(this.clientId);
		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Received ID" + this.clientId + " CPU Speed: " + rating + " [Updated Records]");
		return transaction;
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
