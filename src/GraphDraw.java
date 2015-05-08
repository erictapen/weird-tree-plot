import java.util.ArrayList;
import java.util.Vector;

import fileProcessing.SortedGraph;
import graph.GraphNode;
import plot.GraphPlotter;
import processing.core.*;

@SuppressWarnings("serial")
public class GraphDraw extends PApplet {
	PApplet parent; // The parent PApplet that we will render ourselves onto
	
	
	private GraphPlotter pltr;
	private GraphNode root;
	private double drawRootSize = 10.0;

	public void setup() {
		size(200,200);
		background(0xFFFFFF);

		root = SortedGraph.importFile(
				"/home/justin/Dropbox/java/Wikipedia Crawl/wiki_sorted_test.dot", 
				"sprachliche Einheit");
				
		/*root = new GraphNode("Philosophie");
		root.setSize(1.0);
		root.setxPos(0.0);
		root.setyPos(0.0);
		root.addChild(new GraphNode("Bla"));
		root.getChildren().get(0).setSize(0.8);
		root.getChildren().get(0).setxPos(4.4);
		root.getChildren().get(0).setyPos(2.5);
		root.addChild(new GraphNode("Blubb"));
		root.getChildren().get(1).setSize(0.5);
		root.getChildren().get(1).setxPos(2.4);
		root.getChildren().get(1).setyPos(8.5);
		*/
		
		System.out.println("rootnode is: " + root.getCaption());
		pltr = new GraphPlotter(root, true);
		System.out.println("root has " + root.getChildren().size() + " children.");
	}

	public void draw() {
		System.out.println("Start drawing: ");
		pltr.update();
		ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
		ArrayList<GraphNode> temp = new ArrayList<GraphNode>();
		nodes.add(root);
		while(!nodes.isEmpty()) {
			for(GraphNode x : nodes) {
				//draw the node + every link
				ellipse((float) (width/2.0 + x.getxPos()*drawRootSize), 
						(float) (height/2.0 + x.getyPos()*drawRootSize),
						(float) (x.getSize()*drawRootSize*2.0),
						(float) (x.getSize()*drawRootSize*2.0));
				System.out.println(x);
				for(GraphNode y : x.getChildren()) {
					PVector v1;
					PVector v2;
					PVector vNorm1;
					PVector vNorm2;
					v1 = new PVector(	width/2 + (float)(x.getxPos()*drawRootSize), 
										height/2 + (float)(x.getyPos()*drawRootSize));
					v2 = new PVector(	width/2 + (float)(y.getxPos()*drawRootSize), 
										height/2 + (float)(y.getyPos()*drawRootSize));
					vNorm1 = v1.get();
					vNorm1.sub(v2);
					vNorm1.normalize();
					vNorm2 = vNorm1.get();
					vNorm1.mult((float)(x.getSize()*drawRootSize));
					v1.sub(vNorm1);
					vNorm2.mult((float)(y.getSize()*drawRootSize));
					v2.add(vNorm2);
					line(v1.x, v1.y, v2.x, v2.y);
					
					
					
					
				}
				temp.addAll(x.getChildren());
			}
			nodes.clear();
			nodes.addAll(temp);
			temp.clear();
		}
		//ellipse(width/2, height/2, 100, 100);
		System.out.println(" :drawing completed\n");

	}
}
