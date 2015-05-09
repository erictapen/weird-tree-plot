package graph;

import java.util.ArrayList;

public class GraphNode {
	private String caption;
	private GraphNode parent;
	private ArrayList<GraphNode> children = new ArrayList<GraphNode>();
	private int numberOfAllLeafs;
	private double xPos;
	private double yPos;
	private double size = 1.0;
	
	

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
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
				+ ", size=" + size + ", checked=" + checked + "]";
	}

	/** Detects an intersection
	 * @param anyNode The GraphNode which is needed to test against an intersection
	 * @return Either -1 (if there ist any intersection) or the rad direction in which this needs to move.
	 */
	public double[] intersect(GraphNode n) {
		if(this==n) return new double[] {0.0, 0.0};
		if(Math.sqrt(	Math.pow(this.xPos - n.getxPos(), 2)
						+ Math.pow(this.yPos - n.getyPos(), 2))
						<
						this.size + n.getSize()) {
			return new double[] {n.getxPos()-this.xPos, n.getyPos()-this.yPos};
		}
		return new double[] {0.0, 0.0};
	}

	
	
	
}
