package start.server;

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
