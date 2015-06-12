package graph;

import java.util.ArrayList;
import java.util.Stack;

public class GraphNode {
	private String caption;
	private GraphNode parent;
	private ArrayList<GraphNode> children = new ArrayList<GraphNode>();
	private int numberOfAllLeafs;
	private double xPos;
	private double yPos;
	private double radius = 1.0;
	private ArrayList<ArrayList<Double>> memoryOfMovements;
	
	public boolean sentMessage = false; //testing

	

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

	public int getNumberOfAllLeafs() {
		return numberOfAllLeafs;
	}

	public void setNumberOfAllLeafs(int numberOfAllLeafs) {
		this.numberOfAllLeafs = numberOfAllLeafs;
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
	
	/**Recursive
	 * 
	 */
	public int updateNumberOfAllLeafs() {
		if(children.isEmpty()) return 1;
		int res = 1;
		int temp = 0;
		for(GraphNode x : children) {
			temp = x.updateNumberOfAllLeafs();
			res += temp;
		}
		this.setNumberOfAllLeafs(res);
		return res;
	}
	
	


	@Override
	public String toString() {
		return "GraphNode [caption=" + caption + ", parent=" + parent
				+ ", children=" + children.size() + ", numberOfAllLeafs="
				+ numberOfAllLeafs + ", xPos=" + xPos + ", yPos=" + yPos
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
		}/*
		if(this.parent==null || this.parent==n) return new double[] {0.0, 0.0};
		double[] a = this.getVectorToParent();
		double[] b = new double[] {	n.getxPos()-this.parent.getxPos(),     //from parent to n
									n.getyPos()-this.parent.getyPos()};
		double[] c = new double[] {	n.getxPos()-this.xPos,                 //from this to n
									n.getyPos()-this.yPos};
		if( !((a[0]*b[0]+a[1]*b[1]) * (a[0]*c[0]+a[1]*c[1]) < 0) ) return new double[] {0.0, 0.0};
		double blength = Math.sqrt(b[0]*b[0] + b[1]*b[1]);
		if(Math.sin(Math.atan2(a[0], a[1]) - Math.atan2(b[0], b[1])) * blength < n.getSize()) {
			return new double[] {	0, 0};
		}*/
		return new double[] {0.0, 0.0};
	}

	public double[] getVectorToParent() {
		if(parent!=null) return new double[] {	this.parent.getxPos() - this.xPos, 
												this.parent.getyPos() - this.yPos};
		return new double[] {0.0, 0.0};
	}

	
	
	
}
