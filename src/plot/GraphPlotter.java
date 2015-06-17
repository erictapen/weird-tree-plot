package plot;

import java.util.ArrayList;
import java.util.HashSet;

import fileProcessing.SortedGraph;
import graph.GraphNode;

public class GraphPlotter {

	/** This is the root Node, where everything starts. needs to be init by hand, 
	 * this is not obvious from looking at your inputfile!
	 * 
	 */
	private GraphNode root;
	/** Doesn't have any meaning atm
	 * 
	 */
	private boolean debug;
	/** The manager, which is responsible for getting better solutions for collision detection
	 * 
	 */
	private NodeSetManager manager;
	/** Every node will travel with this stepsize. It will determine the accuracy of the result.
	 *  For smaller stepsize, you'll need more iterations
	 */
	private double stepsize;       
	/** Maximum of steps, a single node will travel. After that, it stops where it is.
	 * 
	 */
	private int maxIteration;
	/** iterator for local use
	 * 
	 */
	private int iteration;
	/** every redrawInterval*iterations, the thread of control will return to the PApplet, in order 
	 * to update the live drawing of the plot
	 *  
	 */
	private int redrawInterval;
	/** This is the distance to 0;0, from which every Node starts its journey. 
	 * Should be big enough to not intersect with any already plotted Nodes!
	 * The bigger it is, the more iterations you will need.
	 */
	private double movingCircleRadius;

	private HashSet<GraphNode> plottedNodes;
	private HashSet<GraphNode> movingNodes;
	private HashSet<GraphNode> waitingNodes;
	private double sizeOffSet;
	private int minNodeLeafs;

	private double minStepSizeBeforeAbort = 0.02;
	private int persistenceBeforeAbort = 500;






	/** Also calls init()
	 * @param root
	 * @param debug
	 */
	public GraphPlotter(GraphNode root, boolean debug) {
		super();
		this.root = root;
		this.debug = debug;
		this.manager = new NodeSetManager(this);
		this.plottedNodes = new HashSet<GraphNode>();
		this.movingNodes = new HashSet<GraphNode>();
		this.waitingNodes = new HashSet<GraphNode>();
		this.init();
	}

	public void init() {

		// TODO Do some initialisation stuff. In this method, everything from getting the root
		//		to a plottable Graph and a usable NodeSetManager must happen!
		//root.updateNumberOfAllLeafs();  //Should already happen at fileimport!
		System.out.println("root has " + root.getNumberOfAllLeafs() + " leafs.");
		this.updateSizes();
		manager.init();
		SortedGraph.exportFile(root, "../data/wiki_sorted_attr.dot", true);

		root.setxPos(0.0);
		root.setyPos(0.0);
		root.setRadius(1.0);
		this.movingNodes.add(root);
		this.waitingNodes.addAll(root.getChildren());
		
		
		
	}

	/** Here does the plotting happen.
	 * 
	 */
	public void update() {
		if(this.redrawInterval == 0) this.redrawInterval = this.maxIteration;
		if(this.waitingNodes.isEmpty()) return;
		
		if(this.iteration==0) {    	
			//if new round begins:
			//update nodeLists, get new nodes, initialize the starting circle
			this.plottedNodes.addAll(movingNodes);
			this.manager.update(movingNodes);
			this.movingNodes.clear();
			GraphNode smallest = root;
			for(GraphNode x : this.waitingNodes) {
				if(smallest.getNumberOfAllLeafs() > x.getParent().getNumberOfAllLeafs()) 
							smallest = x.getParent();
			}
			this.movingNodes.addAll(smallest.getChildren());
			this.waitingNodes.removeAll(smallest.getChildren());
			for(GraphNode x : smallest.getChildren()) {
				this.waitingNodes.addAll(x.getChildren());
			}
			HashSet<GraphNode> tooSmall = new HashSet<GraphNode>();
			for(GraphNode x : this.waitingNodes) {
				if(minNodeLeafs > x.getNumberOfAllLeafs()) tooSmall.add(x);
			}
			this.waitingNodes.removeAll(tooSmall);
			
			this.waitingNodes.removeAll(tooSmall);
			for(GraphNode x : this.movingNodes) {       //do the movingCircle
				double rad = 	Math.atan2(x.getParent().getxPos(), x.getParent().getyPos())
								+ Math.random()*this.stepsize - this.stepsize*0.5;
				if(x.getParent()==this.root) rad = Math.random()*Math.PI*2.0;
				x.setxPos(Math.sin(rad)*this.movingCircleRadius);
				x.setyPos(Math.cos(rad)*this.movingCircleRadius);
			}
			/*
			for(GraphNode x : this.waitingNodes) {       //do the waitingCircle
				double rad = 	Math.atan2(x.getParent().getxPos(), x.getParent().getyPos())
								+ Math.random()*this.stepsize - this.stepsize*0.5;
				x.setxPos(Math.sin(rad)*this.waitingCircleRadius);
				x.setyPos(Math.cos(rad)*this.waitingCircleRadius);
			}
			*/
			for(GraphNode x : this.movingNodes) {
				x.setMemoryOfMovements(new ArrayList<ArrayList<Double>>());
			}
		}
		for(int i=0; i<this.redrawInterval; i++) {
			for(GraphNode movingNode : this.movingNodes) {
				boolean abort = false;
				if(movingNode.getMemoryOfMovements().size() >= this.persistenceBeforeAbort + 1) {
					
					double traveledDist = Math.sqrt(
							Math.pow(
									movingNode.getMemoryOfMovements().get(persistenceBeforeAbort).get(0), 
									2.0)
							+ Math.pow(
									movingNode.getMemoryOfMovements().get(persistenceBeforeAbort).get(0), 
									2.0));
					if(traveledDist <= this.minStepSizeBeforeAbort) abort = true;
					//if(abort) continue;
				}
				if(!abort) {
					double[] vIntersect = new double[2];
					for(GraphNode anyNode : this.manager.getNearbyNodes(movingNode)) {
						double[] v = movingNode.intersect(anyNode);
						vIntersect[0] += v[0];
						vIntersect[1] += v[1];
					}
					double radCenter = Math.atan2(movingNode.getxPos(), movingNode.getyPos());
					if(vIntersect[0]!=0 || vIntersect[1]!=0) {   //in case of intersection
						double radIntersect = Math.atan2(vIntersect[0], vIntersect[1]);
						//push against the direction, where the intersction occurs
						//push away from the center
						movingNode.setxPos(	movingNode.getxPos()
											- Math.sin(radIntersect)*this.stepsize
											+ Math.sin(radCenter)*this.stepsize);
						movingNode.setyPos(	movingNode.getyPos()
											- Math.cos(radIntersect)*this.stepsize
											+ Math.cos(radCenter)*this.stepsize);
					} else {       //in case of no intersection
						double radParent = Math.atan2(	movingNode.getParent().getxPos()
														- movingNode.getxPos(), 
														movingNode.getParent().getyPos()
														- movingNode.getyPos());
						//pull towards parent
						//pull towards center
						radCenter += (Math.random()-0.5)*0.05*Math.PI;
						movingNode.setxPos( movingNode.getxPos()
											+ Math.sin(radParent)*this.stepsize
											- Math.sin(radCenter)*this.stepsize);
						movingNode.setyPos( movingNode.getyPos()
											+ Math.cos(radParent)*this.stepsize
											- Math.cos(radCenter)*this.stepsize);
					}
					movingNode.getMemoryOfMovements().add(0, 
							new ArrayList<Double>());
					movingNode.getMemoryOfMovements().get(0).add(0, movingNode.getxPos());
					movingNode.getMemoryOfMovements().get(0).add(1, movingNode.getyPos());
				}
			}
			this.manager.update();   //to update the node searching architecture
			
			iteration++;
			if(this.iteration == this.maxIteration) {    	//if one round is finished
				this.iteration = 0;
				for(GraphNode x : this.movingNodes) {		//saves memory
					x.setMemoryOfMovements(null);
				}
				return;
			}
		}

	}



	/** Updates all the size fields in the tree, according to this.getSizeFromLeafs(int)
	 * @param root
	 */
	public void updateSizes() {
		HashSet<GraphNode> nodes = new HashSet<GraphNode>();
		HashSet<GraphNode> temp  = new HashSet<GraphNode>();
		nodes.add(this.root);
		while(!nodes.isEmpty()) {
			for(GraphNode x : nodes) {
				x.setRadius(this.getSizeFromLeafs(x.getNumberOfAllLeafs()));
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
		return Math.sqrt((((double) n + sizeOffSet)/ ((double) root.getNumberOfAllLeafs() + sizeOffSet)));
	}


	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/** Stepsize must be about 10 times smaller than the smallest
	 * @param stepsize
	 */
	public void setStepsize(double stepsize) {
		this.stepsize = stepsize;
	}

	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}

	public void setRedrawInterval(int redrawInterval) {
		this.redrawInterval = redrawInterval;
	}

	public HashSet<GraphNode> getPlottedNodes() {
		return plottedNodes;
	}

	public void addPlottedNode(GraphNode plottedNode) {
		this.plottedNodes.add(plottedNode);
	}

	public HashSet<GraphNode> getMovingNodes() {
		return movingNodes;
	}

	public void addMovingNode(GraphNode movingNode) {
		this.movingNodes.add(movingNode);
	}

	public HashSet<GraphNode> getWaitingNodes() {
		return waitingNodes;
	}

	public void addWaitingNode(GraphNode waitingNode) {
		this.waitingNodes.add(waitingNode);
	}

	public void setMovingCircleRadius(double movingCircleRadius) {
		this.movingCircleRadius = movingCircleRadius;
	}

	public int getMaxIteration() {
		return maxIteration;
	}

	public int getIteration() {
		return iteration;
	}

	public NodeSetManager getManager() {
		return manager;
	}

	public void setSizeOffSet(double sizeOffSet) {
		this.sizeOffSet = sizeOffSet;
	}

	public void setMinNodeLeafs(int minNodeLeafs) {
		this.minNodeLeafs = minNodeLeafs;
	}

	public double getMovingCircleRadius() {
		return movingCircleRadius;
	}

	public GraphNode getRoot() {
		return root;

	}

	public void setMinStepSizeBeforeAbort(double minStepSizeBeforeAbort) {
		this.minStepSizeBeforeAbort = minStepSizeBeforeAbort;
	}

	public void setPersistenceBeforeAbort(int persistenceBeforeAbort) {
		this.persistenceBeforeAbort = persistenceBeforeAbort;
	}
	
	
}
