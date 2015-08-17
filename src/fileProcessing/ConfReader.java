package fileProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import draw.GraphDraw;
import processing.core.PApplet;




public class ConfReader {
	
	/** Hashmap, which contains all Key/Value pairs in the configfile
	 * 
	 */
	private HashMap<String, String> content;
	private String errorMsgInteger;
	private String errorMsgDouble;
	
	
	private enum Value {
	    INTEGER, STRING, DOUBLE
	}
	
	public ConfReader(String file) {
		this.content = new HashMap<String, String>();
		System.out.println("Reading config file " + file + " ...");
		int count = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				count++;
				if(line.startsWith("#")) continue;
				if(line.isEmpty()) continue;
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

	private Object getValueByKey(String key, Value val, Object defaultValue) {
		String str = this.content.get(key);
		if(str==null) {
			System.out.println("There was a problem with the attribute " + key 
					+ ". Please check, if it fits the type " + val.toString() + ".");
			return defaultValue;
		} else {
			switch(val) {
				case INTEGER:
					int resI = (int) defaultValue;
					try{
						resI = Integer.parseInt(str);
					} catch(NumberFormatException e) {
						System.out.println(this.errorMsgInteger);
					}
					return resI;
				case DOUBLE:
					double resD = (double) defaultValue;
					try{
						resD = Double.parseDouble(str);
					} catch(NumberFormatException e) {
						System.out.println(this.errorMsgDouble);
					}
					return resD;
				case STRING:
					return str;
					
			}
		}
		return null; //unreachable
	}
	
	public void setupGraphDraw(GraphDraw obj) {
		int sizex = (int)this.getValueByKey("DRAWdisplayWidth", Value.INTEGER, 512);
		int sizey = (int)this.getValueByKey("DRAWdisplayHeight", Value.INTEGER, 512);
		if(sizey==0) sizey = sizex; //this is specified in plotter_example.conf
		obj.size(sizex, sizey);
		
		obj.setInputDOTfile((String)this.getValueByKey("GRAPHinputDOTfile", Value.STRING, "data/input.dot"));
		
	}

}
