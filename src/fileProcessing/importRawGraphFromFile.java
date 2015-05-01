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
	private static ArrayList<GraphNode> nodes;		//list of all nodes
	private static HashMap<String, GraphNode> nodemap = new HashMap<String, GraphNode>(4000000, (float) 0.75);
	private static ArrayList<ArrayList<GraphNode>> differentGraphs; //List of all roots, where graphs do not intersect
	public static void main(String[] args) {
		
		importFile(filename);
		analyseGraph();
	}

	private static void analyseGraph() {
		for(GraphNode x : nodes) {
			if(x.isChecked()) continue;
			ArrayList<GraphNode> pathToRoot = paintGraph(x);
			
		}
		
	}

	private static ArrayList<GraphNode> paintGraph(GraphNode x) {
		ArrayList<GraphNode> res = new ArrayList<GraphNode>();
		ArrayList<GraphNode> path = new ArrayList<GraphNode>();
		GraphNode cursor = x;
		path.add(cursor);
		//TODO
		while(cursor.getParent()!=null && !path.contains(cursor.getParent())) {
			cursor = cursor.getParent();
			path.add(cursor);
		}
		return null;
	}

	private static void importFile(String ifile) {
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(ifile))) {
			String line;
			int i=0;
			while ((line = br.readLine()) != null) {
				if(i%10 == 0) createGraphFromLine(line);
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
