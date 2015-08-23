package fileProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import plot.GraphPlotter;
import plot.NodeSetManager;
import draw.GraphDraw;




public class ConfReader {
	
	/** Hashmap, which contains all Key/Value pairs in the configfile
	 * 
	 */
	private HashMap<String, String> content;
	private ArrayList<String> allowedKeys;
	
	public ConfReader(String file) {
		this.content = new HashMap<String, String>();
		
		this.allowedKeys = new ArrayList<String>();
		this.allowedKeys.add("DRAWdisplayWidth");
		this.allowedKeys.add("DRAWdisplayHeight");
		this.allowedKeys.add("GRAPHinputDOTfile");
		this.allowedKeys.add("GRAPHrootCaption");
		this.allowedKeys.add("GRAPHoutputTEXfile");
		this.allowedKeys.add("DRAWrootSize");
		this.allowedKeys.add("DRAWlines");
		this.allowedKeys.add("DRAWeveryNumberOfUpdates");
		
		this.allowedKeys.add("PLOTTERredrawInterval");
		this.allowedKeys.add("PLOTTERmaxIteration");
		this.allowedKeys.add("PLOTTERstepSize");
		this.allowedKeys.add("PLOTTERmovingCircleRadius");
		this.allowedKeys.add("PLOTTERsizeOffSet");
		this.allowedKeys.add("PLOTTERminNodeLeafs");
		this.allowedKeys.add("PLOTTERminStepSizeBeforeAbort");
		this.allowedKeys.add("PLOTTERpersistenceBeforeAbort");
		
		this.allowedKeys.add("NODESETMANAGERgridSize");		
		
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
				if(!this.allowedKeys.contains(pair[0])) {
					System.out.println("Semantic error on line " +  count + "! " + pair[0] 
							+ " is unknown!");
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
		System.out.println(count + " lines of config read.");
	}
	
	/** Reads an Integer value from config
	 * @param key The key of the key/value pair
	 * @param defaultValue If no entry is set, use this value
	 * @return The value
	 */
	private int getIntegerByKey(String key, int defaultValue) {
		String str = this.content.get(key);
		if(str==null) {
			System.out.println("There was a problem with the attribute " + key 
					+ ". Defaultvalue " + defaultValue + " will be used.");
			return defaultValue;
		} else {
			int resI = (int) defaultValue;
			try{
				resI = Integer.parseInt(str);
			} catch(NumberFormatException e) {
				System.out.println("Error in " + key + "! " + str+ " is not an Integer!");
			}
			return resI;
				
		}
	}
	
	/** Reads an Double value from config
	 * @param key The key of the key/value pair
	 * @param defaultValue If no entry is set, use this value
	 * @return The value
	 */
	private double getDoubleByKey(String key, double defaultValue) {
		String str = this.content.get(key);
		if(str==null) {
			System.out.println("There was a problem with the attribute " + key 
					+ ". Defaultvalue " + defaultValue + " will be used.");
			return defaultValue;
		} else {
			double resD = (double) defaultValue;
			try{
				resD = Double.parseDouble(str);
			} catch(NumberFormatException e) {
				System.out.println("Error in " + key + "! " + str+ " is not an Double!");
			}
			return resD;
		}
	}
	
	/** Reads an Boolean value from config
	 * @param key The key of the key/value pair
	 * @param defaultValue If no entry is set, use this value
	 * @return The value
	 */
	private boolean getBooleanByKey(String key, boolean defaultValue) {
		String str = this.content.get(key);
		if(str==null) {
			System.out.println("There was a problem with the attribute " + key 
					+ ". Defaultvalue " + defaultValue + " will be used.");
			return defaultValue;
		} else {
			boolean resB = (boolean) defaultValue;
			try{
				resB = Boolean.parseBoolean(str);
			} catch(NumberFormatException e) {
				System.out.println("Error in " + key + "! " + str+ " is not an Boolean!");
			}
			return resB;
		}
	}

	/** Reads an String value from config
	 * @param key The key of the key/value pair
	 * @param defaultValue If no entry is set, use this value
	 * @return The value
	 */
	private String getStringByKey(String key, String defaultValue) {
		String str = this.content.get(key);
		if(str==null) {
			System.out.println("There was a problem with the attribute " + key 
					+ ". Defaultvalue " + defaultValue + " will be used.");
			return defaultValue;
		} else {
			return str;
		}
	}
	
	/** This initializes a GraphDraw object. 
	 * Every new configurable field must be mentioned here.
	 * @param obj The GraphDraw object
	 */
	public void setupGraphDraw(GraphDraw obj) {
		int sizex = this.getIntegerByKey("DRAWdisplayWidth", 512);
		int sizey = this.getIntegerByKey("DRAWdisplayHeight", sizex);
		if(sizey==0) sizey = sizex; //this is specified in plotter_example.conf
		obj.size(sizex, sizey);
		obj.setInputDOTfile(this.getStringByKey("GRAPHinputDOTfile", "data/input.dot"));
		obj.setRootCaption(this.getStringByKey("GRAPHrootCaption", "Philosophie"));
		obj.setExportfile(this.getStringByKey("GRAPHoutputTEXfile", "out/out.tex"));
		obj.setDrawRootSize(this.getDoubleByKey("DRAWrootSize", 75.0));
		obj.setDrawLines(this.getBooleanByKey("DRAWlines", false));
		obj.setDrawEveryUpdateInterval(this.getIntegerByKey("DRAWeveryNumberOfUpdates", 10));
	}
	
	/** This initializes a GraphPlotter object. 
	 * Every new configurable field must be mentioned here.
	 * @param obj The GraphPlotter object
	 */
	public void setupGraphPlotter(GraphPlotter obj) {
		obj.setRedrawInterval(this.getIntegerByKey("PLOTTERredrawInterval", 200));
		obj.setMaxIteration(this.getIntegerByKey("PLOTTERmaxIteration", 2000));
		obj.setStepsize(this.getDoubleByKey("PLOTTERstepSize", 0.01));
		obj.setMovingCircleRadius(this.getDoubleByKey("PLOTTERmovingCircleRadius", 10));
		obj.setSizeOffSet(this.getDoubleByKey("PLOTTERsizeOffSet", 0.0));
		obj.setMinNodeLeafs(this.getIntegerByKey("PLOTTERminNodeLeafs", 1));
		obj.setMinStepSizeBeforeAbort(this.getDoubleByKey("PLOTTERminStepSizeBeforeAbort", 0.02));
		obj.setPersistenceBeforeAbort(this.getIntegerByKey("PLOTTERpersistenceBeforeAbort", 500));
	}
	
	/** This initializes a NodeSetManager object. 
	 * Every new configurable field must be mentioned here.
	 * @param obj The NodeSetManager object
	 */
	public void setupNodeSetManager(NodeSetManager obj) {
		obj.setGridsize(this.getDoubleByKey("NODESETMANAGERgridSize", 0.01));
	}

}
