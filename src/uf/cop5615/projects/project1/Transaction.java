package uf.cop5615.projects.project1;


/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011
 * University of Florida
 *
 */
public class Transaction {
	private boolean sorted;
	private int clientId;
	
	public void setClientId(int clientId) {
		this.clientId = clientId;
	}
	
	public int getClientId() {
		return clientId;
	}
	
	public void setSorted(boolean sorted) {
		this.sorted = sorted;
	}
	
	public boolean isSorted() {
		return sorted;
	}	
}
