package plot;

import graph.GraphNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/** This is for (probably) more efficient search of neighbored Nodes in the plane
 * @author justin
 *
 */
public class NodeSetManager {
	private GraphPlotter pltr;
	private double gridsize;
	private HashMap<Vector<Integer>, HashSet<GraphNode>> plottedNodesMap;	
	
	
	public NodeSetManager(GraphPlotter pltr) {
		super();
		this.pltr = pltr;
		this.plottedNodesMap = new HashMap<Vector<Integer>, HashSet<GraphNode>>();
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public HashSet<GraphNode> getNearbyNodes(GraphNode node) {
		HashSet<GraphNode> res = new HashSet<GraphNode>();
		res.addAll(pltr.getMovingNodes());
		int radius = (int)(node.getRadius()*this.gridsize);
		for(int x=-radius-1; x<=radius+1; x++) {
			for(int y=-radius-1; y<=radius+1; y++) {
				Vector<Integer> vect = new Vector<Integer>(2);
				vect.addElement(new Integer((int)(node.getxPos()*this.gridsize) + x));
				vect.addElement(new Integer((int)(node.getyPos()*this.gridsize) + y));
				HashSet<GraphNode> temp = this.plottedNodesMap.get(vect);
				if(temp!=null) res.addAll(temp);
			}
		}
		
		return res;
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(HashSet<GraphNode> addedNodes) {
		for(GraphNode node : addedNodes) {
			
			int radius = (int)(node.getRadius()*this.gridsize);
			for(int x=-radius-1; x<=radius+1; x++) {
				for(int y=-radius-1; y<=radius+1; y++) {
					Vector<Integer> vect = new Vector<Integer>(2);
					vect.addElement(new Integer((int)(node.getxPos()*this.gridsize) + x));
					vect.addElement(new Integer((int)(node.getyPos()*this.gridsize) + y));
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
		this.gridsize = 1.0/gridsize;
	}

	
}
