package plot;

import java.util.ArrayList;

import graph.GraphNode;
import graph.GraphProcessing;

public class GraphPlotter {

	private GraphNode root;
	private boolean debug;
	private NodeSetManager manager;



	/** Also calls init()
	 * @param root
	 * @param debug
	 */
	public GraphPlotter(GraphNode root, boolean debug) {
		super();
		this.root = root;
		this.debug = debug;
		this.init();
	}

	public void init() {

		// TODO Do some initialisation stuff. In this method, everything from getting the root
		//		to a plottable Graph and a usable NodeSetManager must happen!
		root.updateNumberOfAllLeafs();
		this.updateSizes();
		manager.init();


	}

	/** Here will the plotting happen.
	 * 
	 */
	public void update() {

	}

	public GraphNode getRoot() {
		return root;
	}


	/** Updates all the size fields in the tree, according to this.getSizeFromLeafs(int)
	 * @param root
	 */
	public void updateSizes() {
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		ArrayList<GraphNode> temp  = new ArrayList<GraphNode>();
		nodes.add(this.root);
		while(!nodes.isEmpty()) {
			for(GraphNode x : nodes) {
				x.setSize(this.getSizeFromLeafs(x.getNumberOfAllLeafs()));
				temp.addAll(x.getChildren());
			}
			nodes.clear();
			nodes.addAll(temp);
			temp.clear();
		}
	}
	
	/** Here sits the transformation function which defines, how large a GraphNode will be plotted, 
	 * compared to root. It may be necessary to define very complex functions in an external file.
	 * @param n Number of Leafs of a GraphNode
	 * @return The actual size in ] 0.0 ; 1.0 [
	 */
	private double getSizeFromLeafs(int n) {
		return (double) n / (double) root.getNumberOfAllLeafs();
	}
	

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
}
