

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public final class PropertyManager {
	
	public static HashMap<String, String> getProperties() {
		return properties;
	}
	
	private static final HashMap<String, String> properties = new HashMap<String, String>();

	static {
			Log.write("Reading properties from file...");

			StringBuilder text = new StringBuilder();
			String path = System.getProperty("user.dir");
			String filename = path + "/system.properties";
			Scanner scanner = null;
			try {
				scanner = new Scanner(new FileInputStream(filename));
				while (scanner.hasNextLine()){
					String line = scanner.nextLine();
					if (line.startsWith("#")) {
						continue;
					}
					
					StringTokenizer st = new StringTokenizer(line, "=");
					String key = st.nextToken();
					String value = st.nextToken();

					properties.put(key, value);
					text.append(line + Common.NL);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			finally{
				if (scanner != null) {
					scanner.close();
				}
			}
			Log.write("Properties found in file: " + filename + Common.NL + text.toString());
			
	}
	
	private PropertyManager() {}
}
