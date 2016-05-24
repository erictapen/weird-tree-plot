package graph;

import java.util.ArrayList;
import java.util.HashSet;

public class GraphNode {
	private String caption;
	private GraphNode parent;
	private ArrayList<GraphNode> children = new ArrayList<GraphNode>();

	/** The size of the subtree under the node. For example, a leaf always has treeSize=0.
	 * 
	 */
	private int treeSize;
	private double xPos;
	private double yPos;
	private double radius = 0.0;
	private ArrayList<ArrayList<Double>> memoryOfMovements;
	private boolean plotted = false;
	private boolean alreadyHadACollision = false;
	
	public boolean isPlotted() {
		return plotted;
	}

	public void setPlotted(boolean plotted) {
		this.plotted = plotted;
	}

	public ArrayList<ArrayList<Double>> getMemoryOfMovements() {
		return memoryOfMovements;
	}

	public void setMemoryOfMovements(ArrayList<ArrayList<Double>> memoryOfMovements) {
		this.memoryOfMovements = memoryOfMovements;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	private boolean checked;
	
	public GraphNode(String caption) {
		super();
		this.caption = caption;
		this.setChecked(false);
	}

	public GraphNode getParent() {
		return parent;
	}

	public void setParent(GraphNode parent) {
		this.parent = parent;
	}

	public String getCaption() {
		return caption;
	}

	public ArrayList<GraphNode> getChildren() {
		return children;
	}
	
	public void addChild(GraphNode child) {
		this.children.add(child);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getTreeSize() {
		return treeSize;
	}

	public void setTreeSize(int treeSize) {
		this.treeSize = treeSize;
	}
	
	public double getxPos() {
		return xPos;
	}

	public void setxPos(double xPos) {
		this.xPos = xPos;
	}

	public double getyPos() {
		return yPos;
	}

	public void setyPos(double yPos) {
		this.yPos = yPos;
	}
	
	public boolean isAlreadyHadACollision() {
		return alreadyHadACollision;
	}

	public void setAlreadyHadACollision(boolean alreadyHadACollision) {
		this.alreadyHadACollision = alreadyHadACollision;
	}
	
	/**Recursive
	 * 
	 */
	public int updateTreeSize() {
		if(children.isEmpty()) { 
			this.setTreeSize(1);
			return 1;
		}
		int res = 1;
		int temp = 0;
		for(GraphNode x : children) {
			temp = x.updateTreeSize();
			res += temp;
		}
		this.setTreeSize(res);
		return res;
	}

	@Override
	public String toString() {
		return "GraphNode [caption=" + caption + ", parent=" + parent
				+ ", children=" + children.size() + ", TreeSize="
				+ treeSize + ", xPos=" + xPos + ", yPos=" + yPos
				+ ", radius=" + radius + ", checked=" + checked + "]";
	}
	
	/** Detects an intersection
	 * @param n the Node with which to check
	 * @return a double[] with the direction vector in which to move
	 */
	public double[] intersect(GraphNode n) {
		if(this==n) return new double[] {0.0, 0.0};
		if(Math.sqrt(	Math.pow(this.xPos - n.getxPos(), 2)
						+ Math.pow(this.yPos - n.getyPos(), 2))
						<
						this.radius + n.getRadius()) {
			return new double[] {n.getxPos()-this.xPos, n.getyPos()-this.yPos};
		}
		return new double[] {0.0, 0.0};
	}

	public double[] getVectorToParent() {
		if(parent!=null) return new double[] {	this.parent.getxPos() - this.xPos, 
												this.parent.getyPos() - this.yPos};
		return new double[] {0.0, 0.0};
	}

	/**
	 * @return whole tree under the node as a hashset, including itself
	 */
	public HashSet<GraphNode> getWholeTree() {
		HashSet<GraphNode> res = new HashSet<GraphNode>();
		res.add(this);
		for(GraphNode x : this.children) {
			res.addAll(x.getWholeTree());
		}
		return res;
	}
}
