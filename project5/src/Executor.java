

import java.io.OutputStream;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011 
 * University of Florida
 * 
 */
public class Executor implements Runnable {
	private String host;
	private int id;

	public Executor(String host, int id) {
		this.host = host;
		this.id = id;
	}

	public void run() {
		String path = System.getProperty("user.dir");
		Log.write("Starting Remote Process: Client ID: " + this.id + " on " + this.host, Client.log);
		String[] command = {
				"ssh",
				"-i",
				"id_rsa",
//				"javier.figueroa@" + this.host,
				"figueroa@" + this.host,
				"id_rsa",
				"cd " + path + " ; java start "
//				"cd " + path + " ; java -jar start.jar "
				+ this.id };

		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(command);
			
			Log.write("Status: SUCCESS! Parameters Passed: " + this.id, Client.log) ;

			OutputStream stdin = pr.getOutputStream();
			String carriageReturn = "\n";
			stdin.write(carriageReturn.getBytes());
			stdin.flush();
			stdin.close();

			pr.waitFor();
			int exitValue = pr.exitValue();
			if (exitValue != 0) {
				Log.write("Warning: Remote Process could not start Client ID: "	+ this.id + ", host: " + this.host, Client.log);
			}
		} catch (Exception e) {
			Log.write("Warning: Remote Process could not start Client ID: "	+ this.id + ", host: " + this.host, Client.log);
			e.printStackTrace();
		} 
	}
}

