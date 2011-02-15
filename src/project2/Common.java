package project2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * @author E. Javier Figueroa 
 * COP5615 Spring 2011
 * University of Florida
 *
 */
public class Common {
	public static final String SSH_USER = "figueroa";
	public static final String SSH_PASS = "J4v13r!";
	public static final String[] MACHINES = new String[] { 
		"lin114-01.cise.ufl.edu",
//		"lin114-02.cise.ufl.edu",
		"lin114-03.cise.ufl.edu",
		"lin114-04.cise.ufl.edu",
		"lin114-05.cise.ufl.edu",
		"lin114-06.cise.ufl.edu" 
	};
	
	public static int random(int start, int end) {
		// get the range, casting to long to avoid overflow problems
		long range = (long) end - (long) start + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * new Random().nextDouble());
		int randomNumber = (int) (fraction + start);

		return randomNumber;
	}

	public static int[] insertionSort(int[] arr) {
		int i, j, newValue;
		for (i = 1; i < arr.length; i++) {
			newValue = arr[i];
			j = i;
			while (j > 0 && arr[j - 1] > newValue) {
				arr[j] = arr[j - 1];
				j--;
			}
			arr[j] = newValue;
		}
		
		return arr;
	}
	
	public static String arrayToString(int[] array, String separator) {
	    StringBuffer result = new StringBuffer();
	    if (array.length > 0) {
	        result.append(array[0]);
	        for (int i=1; i<array.length; i++) {
	            result.append(separator);
	            result.append(array[i]);
	        }
	    }
	    
	    return result.toString();
	}
	
	public static int[] stringToArray(String csv) {
		StringTokenizer tokenizer = new StringTokenizer(csv, ",");
		int[] numbers = new int[tokenizer.countTokens()];
		int position = 0;
		while (tokenizer.hasMoreTokens()) {
			numbers[position++] = Integer.parseInt(tokenizer.nextToken());
		}
		
		return numbers;
	}
	

	public static boolean connected(String response, String host, int port) {
		if (!response.startsWith("200")) {
			Log.write("Unable to connect to " + host + ":" + port);
			return false;
		}

		return true;
	}

	public static void sendCommand(String command, BufferedWriter writer)
			throws IOException {
		writer.write(command + "\r\n");
		writer.flush();
	}

	public static boolean replied(String response, String host, int port,
			String code) {
		if (!response.startsWith(code)) {
			Log.write("Failed to talk to server " + host + ":" + port
					+ " retrying...");
			return false;
		}

		return true;
	}
}
