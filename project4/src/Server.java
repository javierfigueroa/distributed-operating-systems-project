

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011 
 * University of Florida
 * 
 */
public class Server{
	
	public Server() throws IOException, InterruptedException {
		Log.write("Server is starting clients...");
		
		StringBuffer message = new StringBuffer();
		message.append(String.format("GROUP MEMBERS %s %s", PropertyManager.getProperties().get("ClientNum"), Common.NL));
		message.append(String.format("# of each member requests %s %s", PropertyManager.getProperties().get("numberOfRequests"), Common.NL));
		message.append("Member ID \t\t Sequence Vector \t\t\t Token Vector \t\t\t Token Queue"+Common.NL);
		message.append("========= \t\t =============== \t\t\t ============ \t\t\t ==========="+Common.NL);
		
		Writer output = null;
		String path = System.getProperty("user.dir");
	    File file = new File(path+"/request.log");
	    try {
			output = new BufferedWriter(new FileWriter(file, true));
			output.write(message.toString());
			output.append('\n');
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		startClients();
	}

	private void startClients() throws IOException, InterruptedException {
		int numberOfClients = Integer.parseInt(PropertyManager.getProperties().get("ClientNum"));
		for (int i=1;i<= numberOfClients;i++) {
			String host = PropertyManager.getProperties().get("Client"+i);
			Executor runner = new Executor(host, i);
			new Thread(runner).start();
			Thread.sleep(500);
		}
	}
}