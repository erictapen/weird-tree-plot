package plot;

import graph.GraphNode;

import java.util.ArrayList;

/** This is for (probably) more efficient search of neighbored Nodes in the plane
 * @author justin
 *
 */
public class NodeSetManager {
	protected GraphPlotter pltr;
	
	
	
	public NodeSetManager(GraphPlotter pltr) {
		super();
		this.pltr = pltr;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	public ArrayList<GraphNode> getNearbyNodes(GraphNode node) {
		ArrayList<GraphNode> res = new ArrayList<GraphNode>();
		res.addAll(pltr.getMovingNodes());
		res.addAll(pltr.getPlottedNodes());
		return res;
	}

	public void update() {
		// TODO Auto-generated method stub
		
	}

}
