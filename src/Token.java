import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author E. Javier Figueroa COP5615 Spring 2011 University of Florida
 * 
 */
public class Token {		
	public synchronized void updateToken(int id, String tokenVector, String tokenQueue) {
		vector.put(id, Client.sequenceVector.get(id));
		
		Log.write("Updating token vector with value: " + tokenVector, Client.log);
		String [] numbers = tokenVector.substring(tokenVector.indexOf("{") + 1, tokenVector.indexOf("}")).replaceAll(" ", "").split(",");

		for (String i : numbers) {
			int key = Integer.parseInt(i.split("=")[0]);
			int value = Integer.parseInt(i.split("=")[1]);
			if (key != id) {
				vector.put(key, value);
			}
		}		

		tokenQueue = tokenQueue.substring(tokenQueue.indexOf("[") + 1, tokenQueue.indexOf("]"));
		Log.write("Updating token queue with value: " + tokenQueue, Client.log);
		String [] queue = tokenQueue.split(",");
		for (int i = queue.length - 1 ; i >= 0 ; i--) {
			if (queue[i].length() > 0 && !this.queue.contains(Integer.parseInt(queue[i]))) {
				this.queue.addFirst(Integer.parseInt(queue[i]));
			}
		}
	}
	
	public synchronized Map<Integer, Integer> getVector() {
		return vector;
	}
	
	public synchronized LinkedList<Integer> getQueue() {
		return queue;
	}

	private Map<Integer, Integer> vector = new HashMap<Integer, Integer>();
	private LinkedList<Integer> queue = new LinkedList<Integer>();
}
