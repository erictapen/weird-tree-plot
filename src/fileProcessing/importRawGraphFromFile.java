package fileProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import graph.GraphNode;

public class importRawGraphFromFile {

	private static String filename = "/home/justin/Dropbox/java/Wikipedia Crawl/wiki_raw.dot";
	private static ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();		//list of all nodes
	private static HashMap<String, GraphNode> nodemap = new HashMap<String, GraphNode>(4000000, (float) 0.75);
	private static ArrayList<ArrayList<GraphNode>> differentGraphs; //List of all rootcircles, where graphs do not intersect

	public static void main(String[] args) {

		importFile(filename);
		analyseGraph();
	}

	private static void analyseGraph() {
		int count = 0;
		for(GraphNode x : nodes) {
			
			if(x.isChecked()) continue;
			ArrayList<GraphNode> pathToRoot = paintGraph(x);
			//differentGraphs.add(pathToRoot);
			count++;
			if(pathToRoot.size()>1) {
				System.out.println("Found graph with following rootcircle/root:");
				for(GraphNode y : pathToRoot) {
					System.out.print("    " + y.getCaption());
				}
				System.out.println();
			}
		}
		System.out.println("There are " + count + " individual graphs in memory.");
	}

	/** "paints" the graph, which means that all connected nodes are recognized and flagged
	 * @param x Attention! x should be checked to not have the flag x.isChecked() set before calling this function!!! 
	 * GraphNode where to start with the process. All Nodes, which are in 
	 * any way connected with x will be flagged
	 * @return The rootcircle of the painted graph
	 */
	//TODO it is not clear if this method is doing what it is supposed to!
	private static ArrayList<GraphNode> paintGraph(GraphNode x) {
		ArrayList<GraphNode> res = new ArrayList<GraphNode>();
		ArrayList<GraphNode> path = new ArrayList<GraphNode>();
		GraphNode cursor = x;
		path.add(cursor);
		while(cursor.getParent()!=null && !path.contains(cursor.getParent())) {
			cursor = cursor.getParent();
			path.add(cursor);
		}
		//rootcircle is extracted
		if (cursor.getParent()!=null) {
			res.addAll(path.subList(path.indexOf(cursor.getParent()), path.size()-1));
		}
		res.add(cursor);
		//all the nodes are flagged ("painted") with node.setChecked(true) in order to detect already visited graphs
		ArrayList<GraphNode> todo = new ArrayList<GraphNode>();
		for(GraphNode n : res) {
			todo.addAll(n.getChildren());
		}
		while(!todo.isEmpty()) {
			if(!todo.get(0).isChecked()) todo.addAll(todo.get(0).getChildren());
			todo.get(0).setChecked(true);
			todo.remove(0);
		}
		return res;
	}

	/** reads all lines from ifile and processes it
	 * @param ifile
	 */
	private static void importFile(String ifile) {


		try (BufferedReader br = new BufferedReader(new FileReader(ifile))) {
			String line;
			int i=0;
			while ((line = br.readLine()) != null) {
				if(i%5 == 0) createGraphFromLine(line); //only due to low RAM on my netbook!!!
				i++;
				if(i%1000 == 0) System.out.println(i);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File \"" + ifile + "\" not found. Abort.");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("IOException. Abort.");
			e.printStackTrace();
			return;
		}

		System.out.println("Graph imported. There are " + nodemap.size() + " nodes in memory.");
	}

	/** takes a line and adds the accounting relation into the whole graphset (ArrayList nodes)
	 * @param line
	 */
	private static void createGraphFromLine(String line) {
		//System.out.println(line);
		String[] str = line.split(" --> ");
		if(str.length!=2) return;
		GraphNode child = nodemap.get(str[0]);
		GraphNode parent = nodemap.get(str[1]);
		if(child==null) {
			child = new GraphNode(str[0]);
			nodemap.put(str[0], child);
		}
		if(parent==null) {
			parent = new GraphNode(str[1]);
			nodemap.put(str[1], parent);
		}
		child.setParent(parent);
		parent.addChild(child);
		nodes.add(parent);
		nodes.add(child);
	}

}
