package fileProcessing;

import graph.GraphNode;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/** Import and Export functionality for .dot-files which are already sorted
 * and are therefore faster readable. There are only .dot-files with a single graph allowed!
 * There is a lot of code just pasted from RawGraph.java
 * @author justin
 *
 */
public class SortedGraph {

	
	//private static ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();		//list of all nodes
	private static HashMap<String, GraphNode> nodemap = new HashMap<String, GraphNode>(4000000, (float) 0.75);
	
	
	/** import a single (!) TreeGraph (!) from file
	 * @param ifile file location
	 * @param rootcaption caption of the desired rootNode
	 * @return the rootNode
	 */
	public static GraphNode importFile(String ifile, String rootcaption) {
		try (BufferedReader br = new BufferedReader(new FileReader(ifile))) {
			String line;
			int i=0;
			while ((line = br.readLine()) != null) {
				if(i%1 == 0) createGraphFromLine(line); //this if condition is 
														//only due to low RAM on my netbook!!!
														//Set i%1 for processing every single line.
				i++;
				if(i%1000 == 0) System.out.println(i);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File \"" + ifile + "\" not found. Abort.");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			System.out.println("IOException. Abort.");
			e.printStackTrace();
			return null;
		}

		System.out.println("Graph imported. There are " + nodemap.size() + " nodes in memory.");
		
		return nodemap.get(rootcaption);
	}
	
	/** takes a line and adds the accounting relation into the whole graphset (ArrayList nodes)
	 * @param line
	 */
	private static void createGraphFromLine(String line) {
		//System.out.println(line);
		line = line.replace("\t", "");  //deletes the tab at the beginning
		String[] str = line.split(" <-- ");
		if(str.length!=2) return;
		GraphNode parent = nodemap.get(str[0]);
		GraphNode child = nodemap.get(str[1]);
		if(child==null) {
			child = new GraphNode(str[1]);
			nodemap.put(str[1], child);
		}
		if(parent==null) {
			parent = new GraphNode(str[0]);
			nodemap.put(str[0], parent);
		}
		child.setParent(parent);
		parent.addChild(child);
		//nodes.add(parent);
		//nodes.add(child);
	}
	
	/** Exports the graph! Every data, which is determined by now will be written into the file
	 * @param root The rootNode where to start. Every other node with posx=0, posy=0 
	 * will be seen as without position data!
	 * @param ofile Filename where to export. File must exist!
	 */
	public static void exportFile(GraphNode root, String ofile) {
		//TODO
	}
}
