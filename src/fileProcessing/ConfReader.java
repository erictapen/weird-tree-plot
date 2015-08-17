package fileProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import plot.GraphPlotter;
import draw.GraphDraw;




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
	
	public void setupGraphDraw(GraphDraw obj) {
		int sizex = this.getIntegerByKey("DRAWdisplayWidth", 512);
		int sizey = this.getIntegerByKey("DRAWdisplayHeight", 512);
		if(sizey==0) sizey = sizex; //this is specified in plotter_example.conf
		obj.size(sizex, sizey);
		obj.setInputDOTfile(this.getStringByKey("GRAPHinputDOTfile", "data/input.dot"));
		obj.setRootCaption(this.getStringByKey("GRAPHrootCaption", "Philosophie"));
		obj.setExportfile(this.getStringByKey("GRAPHoutputTEXfile", "out/out.tex"));
		obj.setDrawRootSize(this.getDoubleByKey("DRAWrootSize", 75.0));
		obj.setDrawLines(this.getBooleanByKey("DRAWlines", false));
		obj.setDrawEveryUpdateInterval(this.getIntegerByKey("DRAWeveryNumberOfUpdates", 10));
	}
	
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

}
