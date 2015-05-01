package graph;

import java.util.ArrayList;

public class GraphNode {
	private String caption;
	private GraphNode parent;
	private ArrayList<GraphNode> children = new ArrayList<GraphNode>();
	private int numberOfAllLeafs;
	
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
	
}
