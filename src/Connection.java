

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author E. Javier Figueroa 11/27/2010 
 * CNT 5106c Fall 2010 University of Florida
 * 
 */
public interface Connection extends Remote {

	String read(int clientId) throws InterruptedException, RemoteException;

	String write(int clientId) throws InterruptedException, RemoteException;

}