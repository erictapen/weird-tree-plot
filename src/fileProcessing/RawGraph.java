package fileProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import graph.GraphNode;

/** This class does much more than importing. It can output a sorted list of all graphs which are defined in the .dot-file
 * @author justin
 *
 */
public class RawGraph {

	private static String filename = "/home/justin/Dropbox/java/Wikipedia Crawl/wiki_raw.dot";
	private static ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();		//list of all nodes
	private static HashMap<String, GraphNode> nodemap = new HashMap<String, GraphNode>(4000000, (float) 0.75);
	//differentGraphs is a List of all rootcircles, where graphs do not intersect
	private static ArrayList<ArrayList<GraphNode>> differentGraphs = new ArrayList<ArrayList<GraphNode>>(); 

	public static void main(String[] args) {
		importFile(filename);
		analyseGraph();
		exportSortedFile("/home/justin/Dropbox/java/Wikipedia Crawl/wiki_raw_sorted.dot");
	}


	/** Outputs the correct sorted graphs in a dotfile
	 * @param ofile The filename.
	 */
	private static void exportSortedFile(String ofile) {
		System.out.println("Start exporting to file.");
		try{
			FileWriter writer = new FileWriter(ofile);
			for(ArrayList<GraphNode> rootcircle : differentGraphs) {
				writer.append(  "digraph ");
				for(GraphNode y : rootcircle) {
					writer.append(y.getCaption());
				}
				writer.append(" {\n");
				for(GraphNode y : rootcircle) {
					if(y.getParent()==null) y.setParent(new GraphNode(""));
					writer.append("\t" + y.getParent().getCaption() + " <-- " + y.getCaption() + "\n");
				}
				for(GraphNode root : rootcircle) {
					ArrayList<GraphNode> start = root.getChildren();
					ArrayList<GraphNode> temp = new ArrayList<GraphNode>();
					start.removeAll(rootcircle);
					while(!start.isEmpty()) {
						for(GraphNode node : start) {
							//TODO hier muss die Magie passieren. Ausgangspunkt: alle richtigen Kinder sind in der Liste
							writer.append("\t" + node.getParent().getCaption() + " <-- " + node.getCaption() + "\n");
							temp.addAll(node.getChildren());
						}
						start.clear();
						start.addAll(temp);
						temp.clear();
					}
				}
				
				
				writer.append("}\n\n");
			}
			writer.close();
		} catch(IOException e)
		{
			e.printStackTrace();
		}
		

	}


	private static void analyseGraph() {
		int count = 0;
		for(GraphNode x : nodes) {

			if(x.isChecked()) continue;
			ArrayList<GraphNode> rootcircle = paintGraph(x);
			differentGraphs.add(rootcircle);
			count++;
			/*if(rootcircle.size()>2) {
				System.out.println("Found graph with following rootcircle/root:");
				for(GraphNode y : rootcircle) {
					System.out.print("    " + y.getCaption());
				}
				System.out.println();
			}*/
		}
		System.out.println("There are " + count + " individual graphs in memory.");
	}

	/** "paints" the graph, which means that all connected nodes are recognized and flagged
	 * @param x Attention! x should be checked to not have the flag x.isChecked() set before calling this function!!! 
	 * GraphNode where to start with the process. All Nodes, which are in 
	 * any way connected with x will be flagged
	 * @return The rootcircle of the painted graph
	 */
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
				if(i%1 == 0) createGraphFromLine(line); //only due to low RAM on my netbook!!!
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
