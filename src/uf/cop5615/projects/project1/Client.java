package uf.cop5615.projects.project1;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011
 * University of Florida
 *
 */
public class Client {
	private String host;
	private int port;
	private int id;
	private int rating;
	private Socket socket;
	private BufferedReader reader;
	private BufferedWriter writer;

	public Client(String host, int port, int id) {
		this.host = host;
		this.port = port;
		this.id = id;

		try {
			work();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			finalize();
		}
	}
	
	@Override 
	public void finalize() {
		try {
			this.socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void work() throws Exception {
		// connect to server
		if (!connect()) {
			String error = String.format("Failed to connect to server => %s:%d", this.host, this.port);
			Log.write(error);
			throw new Exception(error);
		}

		// get random cpu rating and send cpu rating and client id to server
		this.rating = Common.random(1, 10);
		Common.sendCommand(String.format("cpu %d,%d", this.rating, this.id), this.writer);
		Log.writeToFile("Sent: CPU Speed " + this.rating, this.id);
		
		String response = this.reader.readLine();
		if (!Common.replied(response, this.host, this.port, "226")) {
			String error = String.format("Failed to send cpu rating to server => %s:%d", this.host, this.port);
			Log.write(error);
			throw new Exception(error);
		}

		// get numbers to sort from server
		String csvNumbers = response.substring("226 ".length());
		Log.writeToFile("RECV: " + csvNumbers, this.id);	
		int[] numbers = Common.stringToArray(csvNumbers);
		
		// sort numbers and send them back
		Log.writeToFile("Doing insertion sort ...", this.id);
		numbers = Common.insertionSort(numbers);
		csvNumbers = Common.arrayToString(numbers, ",");
		Common.sendCommand(String.format("numbers %s", csvNumbers), this.writer);
		Log.writeToFile("Sent: " + csvNumbers, this.id);
		if (!Common.replied(response, this.host, this.port, "226")) {
			String error = String.format("Failed to send sorted numbers to server => %s:%d", this.host, this.port);
			Log.write(error);
			throw new Exception(error);
		}
		
		// terminate
		Common.sendCommand("BYE", this.writer);
		Log.writeToFile("BYE", this.id);
		this.reader.readLine();
		Log.writeToFile("Terminating Connection! OK!", this.id);
	}

	private boolean connect() throws InterruptedException {
		int attempt = 0;
		while (attempt < 5) {
			attempt++;
			try {
				this.socket = new Socket(this.host, this.port);
				Log.writeToFile("Starting remote connection: SUCCESS!", this.id);
				
				this.reader = new BufferedReader(new InputStreamReader(
						this.socket.getInputStream()));
				this.writer = new BufferedWriter(new OutputStreamWriter(
						this.socket.getOutputStream()));

				String response = this.reader.readLine();
				if (Common.connected(response, this.host, this.port))
					return true;
			} catch (Exception e) {
				Log.write("Failed connection to the server. Attempt connection : " + attempt);
			}
			
			Thread.sleep(1000);
		}

		return false;
	}
}
