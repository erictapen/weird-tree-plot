package plot;

import fileProcessing.ConfReader;
import graph.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/** This is for (probably) more efficient search of neighbored Nodes in the plane
 * @author justin
 *
 */
public class NodeSetManager {
	/** This is the length of a grid cell, in world units.
	 * 
	 */
	private double gridsize;
	

	private HashMap<Vector<Integer>, HashSet<GraphNode>> plottedNodesMap;	
	
	
	public NodeSetManager() {
		super();
		this.plottedNodesMap = new HashMap<Vector<Integer>, HashSet<GraphNode>>();
	}

	public void init() {
		//still nothing to do. This is just here for future support.
	}
	
	public void init(ConfReader cnf) {
		String value;
		
		value = cnf.getValueByKey("NODESETMANAGERgridSize");
		try {
			if(value != null) this.gridsize = Double.parseDouble(value);
			else this.gridsize = 0.0125; //default value
		} catch (NumberFormatException e) {
			System.out.print("Config Syntax Error. " + value + " is not an appropiate value for"
					+ "NODESETMANAGERgridSize.");
		}
		
		init();
	}
	
	
	
	public HashSet<GraphNode> getNearbyNodes(GraphNode node) {
		HashSet<GraphNode> res = new HashSet<GraphNode>();
		int radius = (int)(node.getRadius()/this.gridsize);
		for(int x=-radius-1; x<=radius+1; x++) {
			for(int y=-radius-1; y<=radius+1; y++) {
				Vector<Integer> vect = new Vector<Integer>(2);
				vect.addElement(new Integer((int)(node.getxPos()*this.gridsize) + x));
				vect.addElement(new Integer((int)(node.getyPos()*this.gridsize) + y));
				HashSet<GraphNode> temp = this.plottedNodesMap.get(vect);
				if(temp!=null) res.addAll(temp);
			}
		}
		if(Math.random() < 0.0001) {
			System.out.println("Node " + node.getCaption() + node.getRadius() + " lies near " + res.size()
					+ " other nodes:");
//			for(GraphNode x : res) {
//				System.out.println("\t" + x.getCaption() + "\t " + x.getRadius());
//			}
		}

		return res;
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	/** Add nodes to the managed Set.
	 * @param addedNodes
	 */
	public void update(HashSet<GraphNode> addedNodes) {
		for(GraphNode node : addedNodes) {
			
			int radius = (int)(node.getRadius()/this.gridsize); //nodesize in the grids scale
			for(int x=-radius-1; x<=radius+1; x++) {
				for(int y=-radius-1; y<=radius+1; y++) {
					Vector<Integer> vect = new Vector<Integer>(2);
					vect.add(0, new Integer((int)(node.getxPos()/this.gridsize) + x));
					vect.add(1, new Integer((int)(node.getyPos()/this.gridsize) + y));
					HashSet<GraphNode> temp = this.plottedNodesMap.get(vect);
					if(temp!=null) {
						temp.add(node);
					} else {
						HashSet<GraphNode> newList = new HashSet<GraphNode>();
						newList.add(node);
						this.plottedNodesMap.put(vect, newList);
					}
				}
			}
			
		}
	}

	public void setGridsize(double gridsize) {
		this.gridsize = gridsize;
	}

	public String getStatus() {
		int biggest = 0;
		int smallest = Integer.MAX_VALUE;
		int sum = 0;
		int count = 0;
		for(HashSet<GraphNode> x : plottedNodesMap.values()) {
			if(x.size() > biggest) biggest = x.size();
			if(x.size() < smallest) smallest = x.size();
			sum += x.size();
			count++;
		}
		String keys = "";
		for(Vector<Integer> x : plottedNodesMap.keySet()) keys += "\t\t" + x.toString() 
				+ this.plottedNodesMap.get(x).size()+ "\n";
		double average = 0.0;
		if(count!=0) average = (double)sum/count;
		return "NodeSetManager_Status: " + "\n\t" 
			+ count + " cells" + "\n" 
			+ keys
			+ sum + " nodes" + "\n\t"
			+ biggest + " nodes in the biggest cell" + "\n\t"
			+ smallest + " nodes in the smallest cell" + "\n\t"
			+ average + " nodes in the average cell\n";
		
	}
	
	/** Returns a three-dimensional Vector for every cell, containing the key (0 and 1) and the size (2)
	 * @return
	 */
	public ArrayList<Vector<Integer>> getOverview() {
		ArrayList<Vector<Integer>> res = new ArrayList<Vector<Integer>>();
		Vector<Integer> vect;
		for(Vector<Integer> x : this.plottedNodesMap.keySet()) {
			vect = new Vector<Integer>(3);
			vect.add(0, x.elementAt(0));
			vect.add(1, x.elementAt(1));
			vect.add(2, this.plottedNodesMap.get(x).size());
			res.add(vect);
		}
		return res;
	}
	
	public double getGridsize() {
		return gridsize;
	}
}
