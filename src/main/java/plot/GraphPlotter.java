package plot;

import java.util.ArrayList;
import java.util.HashSet;

import fileProcessing.ConfReader;
import fileProcessing.SortedGraph;
import graph.GraphNode;

@SuppressWarnings("unused")
public class GraphPlotter {

	/** This is the root Node, where everything starts. needs to be init by hand, 
	 * this is not obvious from looking at your inputfile, because you can choose it.
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
	
	private double sizeMethodMix = 0.0;             //There are two different sizeEvaluation methods. 
													//		1.0 means size comes from treeSize,
													//		0.0 means size comes from amount of children
	private int largestChildrenSet = 0;             //biggest amount of children in tree is saved here

	private NodeSetManager movingmanager;

	/** Also calls init()
	 * @param root
	 * @param debug
	 */
	public GraphPlotter(GraphNode root, boolean debug) {
		super();
		this.root = root;
		this.debug = debug;
		this.manager = new NodeSetManager();
		this.movingmanager = new NodeSetManager();
		this.plottedNodes = new HashSet<GraphNode>();
		this.movingNodes = new HashSet<GraphNode>();
		this.waitingNodes = new HashSet<GraphNode>();
		this.init();
	}

	public void init() {
		System.out.println("root has " + root.getTreeSize() + " leafs.");
		this.updateSizes();
		manager.init();

		root.setxPos(0.0);
		root.setyPos(0.0);
		//root.setRadius(1.0);
		this.movingNodes.add(root);
		this.waitingNodes.addAll(root.getChildren());
		this.root.setAlreadyHadACollision(true); //avoid this big pink dot in the middle of the screen
		this.evalLargestChildrenSet();
	}

	/** initializes a file by using a config file. Attributes, which are not in the file 
	 * will be initialized by their default.
	 * @param cnf The config Reader, which holds the data for initialization.
	 */
	public void init(ConfReader cnf) {
		cnf.setupGraphPlotter(this);
		init();
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
				if(smallest.getTreeSize() > x.getParent().getTreeSize()) smallest = x.getParent();
			}
			this.movingNodes.addAll(smallest.getChildren());
			this.waitingNodes.removeAll(smallest.getChildren());
			for(GraphNode x : smallest.getChildren()) {
				this.waitingNodes.addAll(x.getChildren());
			}
			if(minNodeLeafs>1) {
				HashSet<GraphNode> tooSmall = new HashSet<GraphNode>();
				for(GraphNode x : this.waitingNodes) {
					if(minNodeLeafs > x.getTreeSize()) tooSmall.add(x);
				}
				this.waitingNodes.removeAll(tooSmall);
			}
			double movingCircleMinRadius = Math.sqrt(Math.pow(smallest.getxPos(), 2) + Math.pow(smallest.getyPos(), 2))
											+ smallest.getRadius();
			for(GraphNode x : this.movingNodes) {       //do the movingCircle
				double rad = 	Math.atan2(x.getParent().getxPos(), x.getParent().getyPos())
								+ Math.random()*this.stepsize - this.stepsize*0.5;
				if(x.getParent()==this.root) rad = Math.random()*Math.PI*2.0;
				x.setxPos(Math.sin(rad)*(movingCircleMinRadius + this.movingCircleRadius));
				x.setyPos(Math.cos(rad)*(movingCircleMinRadius + this.movingCircleRadius));
			}
			for(GraphNode x : this.movingNodes) {
				x.setMemoryOfMovements(new ArrayList<ArrayList<Double>>());
			}
		}
		
		for(int i=0; i<this.redrawInterval; i++) {
			this.movingmanager = new NodeSetManager();
			this.movingmanager.setGridsize(this.manager.getGridsize());
			this.movingmanager.init();
			this.movingmanager.update(this.movingNodes);
			
			for(GraphNode movingNode : this.movingNodes) {
				if(		(!movingNode.isPlotted()) && 
						(movingNode.getMemoryOfMovements().size() >= this.persistenceBeforeAbort + 1)
				  ) {
					double traveledDist = Math.sqrt(
							Math.pow(
									movingNode.getMemoryOfMovements().get(persistenceBeforeAbort).get(0)
										- movingNode.getxPos(), 
									2.0)
							+ Math.pow(
									movingNode.getMemoryOfMovements().get(persistenceBeforeAbort).get(1)
										- movingNode.getyPos(), 
									2.0));
					if(traveledDist <= this.minStepSizeBeforeAbort) {
						movingNode.setPlotted(true);
					}
				}
				if(!movingNode.isPlotted()) {
					double[] vIntersect = new double[2];
					HashSet<GraphNode> toCheck = this.manager.getNearbyNodes(movingNode);
					toCheck.addAll(movingmanager.getNearbyNodes(movingNode));
					for(GraphNode anyNode : toCheck) {
						double[] v = movingNode.intersect(anyNode);
						vIntersect[0] += v[0];
						vIntersect[1] += v[1];
					}
					double radCenter = Math.atan2(movingNode.getxPos(), movingNode.getyPos());
					radCenter += (Math.random()-0.5)*this.stepsize*0.01*Math.PI;
					if(vIntersect[0]!=0 || vIntersect[1]!=0) {   //in case of intersection
						double radIntersect = Math.atan2(vIntersect[0], vIntersect[1]);
						//push against the direction, where the intersection occurs
						//push away from the center (but not so much)
						movingNode.setxPos(	movingNode.getxPos()
											- Math.sin(radIntersect)*this.stepsize
											+ Math.sin(radCenter)*this.stepsize*0.5);
						movingNode.setyPos(	movingNode.getyPos()
											- Math.cos(radIntersect)*this.stepsize
											+ Math.cos(radCenter)*this.stepsize*0.5);
						movingNode.setAlreadyHadACollision(true);
					} else {       //in case of no intersection
						double radParent = Math.atan2(	movingNode.getParent().getxPos() - movingNode.getxPos(), 
														movingNode.getParent().getyPos() - movingNode.getyPos());
						if(movingNode.isAlreadyHadACollision()) {
							//pull towards parent
							//pull towards center
							movingNode.setxPos( movingNode.getxPos()
												+ Math.sin(radParent)*this.stepsize
												- Math.sin(radCenter)*this.stepsize);
							movingNode.setyPos( movingNode.getyPos()
												+ Math.cos(radParent)*this.stepsize
												- Math.cos(radCenter)*this.stepsize);
						} else {
							//determine alternativeStepsize from the size of the movingNode
							//pull towards parent
							//pull towards center
							movingNode.setxPos( movingNode.getxPos()
												+ Math.sin(radParent)*movingNode.getRadius()*0.49
												- Math.sin(radCenter)*movingNode.getRadius()*0.49);
							movingNode.setyPos( movingNode.getyPos()
												+ Math.cos(radParent)*movingNode.getRadius()*0.49
												- Math.cos(radCenter)*movingNode.getRadius()*0.49);
						}
					}
					movingNode.getMemoryOfMovements().add(0, new ArrayList<Double>());
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
				x.setRadius(this.getSizeFromLeafs(x.getTreeSize(), x.getChildren().size()+1));
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
	private double getSizeFromLeafs(int treeSize, int childSize) {
		return this.sizeMethodMix * Math.sqrt((((double) treeSize + sizeOffSet)/ 
						((double) root.getTreeSize() + sizeOffSet)))
				+
				(1-this.sizeMethodMix) * Math.sqrt((((double) childSize + sizeOffSet)/ 
						((double) this.largestChildrenSet + sizeOffSet)));
	}

	private void evalLargestChildrenSet() {
		HashSet<GraphNode> nodes = new HashSet<GraphNode>();
		HashSet<GraphNode> temp  = new HashSet<GraphNode>();
		nodes.add(this.root);
		while(!nodes.isEmpty()) {
			for(GraphNode x : nodes) {
				if(x.getChildren().size() > this.largestChildrenSet) this.largestChildrenSet = x.getChildren().size();
				temp.addAll(x.getChildren());
			}
			nodes.clear();
			nodes.addAll(temp);
			temp.clear();
		}
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
	
	public NodeSetManager getMovingmanager() {
		return movingmanager;
	}

	public int getMinNodeLeafs() {
		return minNodeLeafs;
	}

	public void setSizeMethodMix(double sizeMethodMix) {
		this.sizeMethodMix = sizeMethodMix;
	}
}
