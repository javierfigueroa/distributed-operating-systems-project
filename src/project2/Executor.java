package project2;

import java.io.OutputStream;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011 
 * University of Florida
 * 
 */
public class Executor implements Runnable {
	private String host;
	private String server;
	private int port;
	private int id;
	private ClientType type;

	public int getId() {
		return id;
	}

	public Executor(String host, String server, int port, int id, ClientType type) {
		this.host = host;
		this.server = server;
		this.port = port;
		this.id = id;
		this.type = type;
	}

	@Override
	public void run() {
		String path = System.getProperty("user.dir");
		Log.write("Starting Remote Process: Client ID: " + this.id + " on "
				+ this.host);
		String[] command = {
				"ssh",
				"-i",
				"id_javier",
				"figueroa@" + this.host,
				"java -jar " + path + "/start.jar " + this.server + " "
						+ this.port + " " + type.toString() + " " + this.id };

		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(command);

			Log.write("Status: SUCCESS! Parameters Passed: " + this.server	+ " " + this.port + " " + this.id);

			OutputStream stdin = pr.getOutputStream();
			String carriageReturn = "\n";
			stdin.write(carriageReturn.getBytes());
			stdin.flush();
			stdin.close();

			pr.waitFor();
			int exitValue = pr.exitValue();
			if (exitValue != 0) {
				Log.write("Warning: Remote Process could not start for " + this.type + " Client ID: "	+ this.id + ", host: " + this.host);
			}
		} catch (Exception e) {
			Log.write("Warning: Remote Process could not start for " + this.type + " Client ID: "	+ this.id + ", host: " + this.host);
			e.printStackTrace();
		} 
	}
}

