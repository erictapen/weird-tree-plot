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
		//todo
	}
	
	public void init(ConfReader cnf) {
		cnf.setupNodeSetManager(this);
		init();
	}
	
	public HashSet<GraphNode> getNearbyNodes(GraphNode node) {
		HashSet<GraphNode> res = new HashSet<GraphNode>();
		int radius = (int)Math.ceil(node.getRadius()/this.gridsize);
		if(radius==0) System.out.println("Radius ist null!");
		for(int x=-radius-1; x<=radius+1; x++) {
			for(int y=-radius-1; y<=radius+1; y++) {
				Vector<Integer> vect = new Vector<Integer>(2);
				vect.addElement(new Integer((int)Math.floor(node.getxPos()/this.gridsize)) + x);
				vect.addElement(new Integer((int)Math.floor(node.getyPos()/this.gridsize)) + y);
				HashSet<GraphNode> temp = this.plottedNodesMap.get(vect);
				if(temp!=null) res.addAll(temp);
			}
		}
		return res;
	}
	
	/** Returns the next node from node or null, if no one is left. Only node centers will be used. 
	 * Manhattan means, that the manhattan metric will be applied.
	 * @param node
	 * @return
	 */
	public GraphNode getNextNodeManhattan(GraphNode node) {
		GraphNode res = null;
		//TODO
		return res;
	}
	
	/** Returns the node, which circle can be fastest reached from pos, using the manhattan metric. Imagine drawing
	 * a series of circles with a pen. At the point where you finished the first one, you want to get from your 
	 * position on the circle as fast as possible to the next circle. Hereby it's only important to look after the
	 * next reachable point on another circle line.
	 * @param pos The position, from where you are looking for the next point. Usually this is an point on your already
	 * drawn circle.
	 * @return
	 */
	public GraphNode getNextNodeArcManhattan(Vector<Double> pos) {
		GraphNode res = null;
		//TODO
		return res;
	}

	public void update() {
	}
	
	/** Add nodes to the managed Set.
	 * @param addedNodes
	 */
	public void update(HashSet<GraphNode> addedNodes) {
		for(GraphNode node : addedNodes) {
			int radius = (int)Math.ceil(node.getRadius()/this.gridsize); //nodesize in the grids scale
			if(radius==0) System.out.println("Radius ist null!");
			for(int x=-radius-1; x<=radius+1; x++) {
				for(int y=-radius-1; y<=radius+1; y++) {
					if(Math.sqrt(x*x + y*y) > radius+1) continue;
					Vector<Integer> vect = new Vector<Integer>(2);
					vect.add(0, new Integer((int)Math.floor(node.getxPos()/this.gridsize)) + x);
					vect.add(1, new Integer((int)Math.floor(node.getyPos()/this.gridsize)) + y);
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
