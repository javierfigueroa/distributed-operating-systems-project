

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
public class Log {
	
	public static void write(String message){
		System.out.println(String.format("%s", message));
	}
		
	public static void write(String message, String filename){
		System.out.println(String.format("%s", message));
		
		Writer output = null;
		String path = System.getProperty("user.dir");
	    File file = new File(path+"/"+filename+".log");
	    try {
			output = new BufferedWriter(new FileWriter(file, true));
			output.write(message);
			output.append('\n');
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void output(int id, String sequenceVector, String tokenVector, String tokenQueue) {
		String message = String.format("%s \t\t\t %s \t\t\t %s \t\t\t %s", id, sequenceVector, tokenVector, tokenQueue);
		System.out.println(String.format("%s", message));
		
		Writer output = null;
		String path = System.getProperty("user.dir");
	    File file = new File(path+"/request.log");
	    try {
			output = new BufferedWriter(new FileWriter(file, true));
			output.write(message);
			output.append('\n');
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}