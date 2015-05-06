import java.util.ArrayList;

import graph.GraphNode;
import plot.GraphPlotter;
import processing.core.*;

@SuppressWarnings("serial")
public class GraphDraw extends PApplet {
	private GraphPlotter pltr;
	private GraphNode root;
	
	public void setup() {
		size(200,200);
		background(0);

		pltr = new GraphPlotter(root, true);
		root = pltr.getRoot();
	}

	public void draw() {
		pltr.update();
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		ArrayList<GraphNode> temp = new ArrayList<GraphNode>();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			for(GraphNode x : nodes) {
				//TODO Draw Node at the right point with the right size
				temp.addAll(x.getChildren());
			}
			nodes.clear();
			nodes.addAll(temp);
			temp.clear();
		}
		
	}
}
