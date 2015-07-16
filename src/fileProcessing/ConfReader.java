package fileProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class ConfReader {
	
	/** Hashmap, which contains all Key/Value pairs in the configfile
	 * 
	 */
	private HashMap<String, String> content;
	
	public ConfReader(String file) {
		this.content = new HashMap<String, String>();
		System.out.println("Reading config file " + file + " ...");
		int count = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				count++;
				if(line.startsWith("#")) continue;
				if(line == "") continue;
				String[] pair = line.split(" = ");
				if(pair.length != 2) {
					System.out.println("Syntax error on line " + count + 
							"! There are only lines of the form \"KEY = VALUE\" allowed!");
					continue;
				}
				content.put(pair[0], pair[1]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("Config file not found! Default values will be used.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("There was a problem with the config file.");
			e.printStackTrace();
		}
		System.out.println(count + "lines of config read.");
	}

	public String getValueByKey(String key) {
		return content.get(key);
	}

}
