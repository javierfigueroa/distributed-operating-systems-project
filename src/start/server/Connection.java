package start.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

import start.Common;
import start.Log;

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
			Server.result.add(number);
		}
		
		return reply(226);
	}

	private int register(StringTokenizer st) throws InterruptedException {
		String content = st.nextToken();
		int rating = Integer.parseInt(content.split(",")[0]);
		this.clientId = Integer.parseInt(content.split(",")[1]);
		
		Server.rating.put(this.socket.getInetAddress().getHostName() + ":" + socket.getPort(), rating);
		Transaction transaction = new Transaction();
		transaction.setClientId(this.clientId);
		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Received ID" + this.clientId + " CPU Speed: " + rating + " [Updated Records]");
		
		//wait until all cpu ratings are updated
		while (Server.rating.containsValue(-1)) {
			Thread.sleep(1000);
		}
		
		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Calculating numbers to send to clientID"+this.clientId);
		Thread.sleep(5000);
		
		// calculate amount of numbers to send
		int cumulativeCPUspeed = 0;
		for (Integer speed : Server.rating.values()) {
			cumulativeCPUspeed += speed;
		}
		
		int ratio = (int) ((rating / (double)cumulativeCPUspeed) * Server.numbers.size());
		
		// compose list of numbers for client
		StringBuilder builder = new StringBuilder();
		int count = 0;
		for (Integer number : Server.numbers.keySet()) {
			if (!Server.numbers.get(number).isSorted()) {
				builder.append(String.valueOf(number));
				count++;
				transaction.setSorted(true);
				Server.numbers.put(number, transaction);
				
				if (count == ratio) break;
				builder.append(",");
			}
		}		

		Log.write(Thread.currentThread().getName() + "(" + this.clientId + "): Assigning " + builder.toString() + " to ClientID" + this.clientId);
		return reply(226, builder.toString());
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
