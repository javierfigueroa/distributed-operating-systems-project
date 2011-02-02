package start.server;

import java.io.IOException;
import java.security.PublicKey;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import start.Common;
import start.Log;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011
 * University of Florida
 *
 */
public class Exec implements Runnable {
	private String host;
	private String server;
	private int port;
	private int id;

	public int getId() {
		return id;
	}

	public Exec(String host, String server, int port, int id) {
		this.host = host;
		this.server = server;
		this.port = port;
		this.id = id;
	}

	@Override
	public void run() {
		String path = System.getProperty("user.dir");
		Log.write("Starting Remote Process: Client ID: " + this.id + " on " + this.host);
		String command = "java -jar "+path+"/start.jar " + this.server	+ " " + this.port + " " + this.id;

		SSHClient ssh = null; 
		try {
			ssh = new SSHClient();
			ssh.setConnectTimeout(1000);
			ssh.setTimeout(1000);
			ssh.loadKnownHosts();
			ssh.addHostKeyVerifier ( 
				    new HostKeyVerifier() { 
						@Override
						public boolean verify(String arg0, int arg1,
								PublicKey arg2) {
							return true;
						} 
				    } 
			);
			
			ssh.connect(this.host);
			ssh.authPassword(Common.SSH_USER, Common.SSH_PASS);
			ssh.startSession().exec(command);
			Log.write("Status: SUCCESS! Parameters Passed: " + this.server + " " + this.port + " " + this.id);
		} catch (Exception e) {
			Log.write("Warning: Remote Process could not start for Client ID: " + this.id + ", host: " + this.host);
			Server.decreaseWorkers();
			e.printStackTrace();
		} finally {
			try {				
				if (ssh != null) {
					ssh.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
