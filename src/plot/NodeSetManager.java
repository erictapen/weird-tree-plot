package plot;

import graph.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/** This is for (probably) more efficient search of neighbored Nodes in the plane
 * @author justin
 *
 */
public class NodeSetManager {
	protected GraphPlotter pltr;
	protected HashMap<Vector<Integer>, ArrayList<GraphNode>> plottedNodesMap;	
	
	public NodeSetManager(GraphPlotter pltr) {
		super();
		this.pltr = pltr;
		this.plottedNodesMap = new HashMap<Vector<Integer>, ArrayList<GraphNode>>();
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public ArrayList<GraphNode> getNearbyNodes(GraphNode node) {
		ArrayList<GraphNode> res = new ArrayList<GraphNode>();
		res.addAll(pltr.getMovingNodes());
		for(int x=-1; x<=1; x++) {
			for(int y=-1; y<=1; y++) {
				Vector<Integer> vect = new Vector<Integer>(2);
				vect.addElement(new Integer((int)node.getxPos() + x));
				vect.addElement(new Integer((int)node.getyPos() + y));
				ArrayList<GraphNode> temp = this.plottedNodesMap.get(vect);
				if(temp!=null) res.addAll(temp);
			}
		}
		
		return res;
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(ArrayList<GraphNode> addedNodes) {
		for(GraphNode x : addedNodes) {
			Vector<Integer> vect = new Vector<Integer>(2);
			vect.addElement(new Integer((int)x.getxPos()));
			vect.addElement(new Integer((int)x.getyPos()));
			ArrayList<GraphNode> temp = this.plottedNodesMap.get(vect);
			if(temp!=null) {
				temp.add(x);
			} else {
				ArrayList<GraphNode> newList = new ArrayList<GraphNode>();
				newList.add(x);
				this.plottedNodesMap.put(vect, newList);
			}
		}
	}

}
