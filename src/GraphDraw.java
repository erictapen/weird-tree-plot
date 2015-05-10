import fileProcessing.SortedGraph;
import graph.GraphNode;
import plot.GraphPlotter;
import processing.core.*;

@SuppressWarnings("serial")
public class GraphDraw extends PApplet {
	PApplet parent; // The parent PApplet that we will render ourselves onto


	private GraphPlotter pltr;
	private GraphNode root;
	private double drawRootSize = 75.0;
	private boolean drawLines = true;
	private int drawEveryUpdateInterval = 10;

	public void setup() {
		size(512, 512);
		background(0xffffff);

		root = SortedGraph.importFile(
				"/home/justin/Dropbox/java/Wikipedia Crawl/wiki_sorted_test_2360.dot", 
				"Geld");
		/*	

		root = new GraphNode("Philosophie");
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
		root.getChildren().get(1).addChild(new GraphNode("haha"));
		root.getChildren().get(1).getChildren().get(0).setSize(0.2);
		root.getChildren().get(1).getChildren().get(0).setxPos(-3.0);
		root.getChildren().get(1).getChildren().get(0).setyPos(-3.0);
		 */


		System.out.println("rootnode is: " + root.getCaption());
		pltr = new GraphPlotter(root, true);
		pltr.setRedrawInterval(0);
		pltr.setMaxIteration(5000);
		pltr.setStepsize(0.01);
		pltr.setMovingCircleRadius(5.0);
		pltr.setWaitingCircleRadius(10.0);
		System.out.println("root has " + root.getChildren().size() + " children.");
	}

	public void draw() {
		background(0xFFFFFF);
		System.out.print(	"Plot in progress: " + pltr.getIteration() + "/" + pltr.getMaxIteration() 
							+ "iterations and " + pltr.getMovingNodes().size() + " movingNodes and "
							+ pltr.getPlottedNodes().size() + "plottedNodes\r");
		for(int i=0; i<drawEveryUpdateInterval; i++) {
			pltr.update();
		}
		System.out.print("Start drawing.                                    \r");
		for(GraphNode x : pltr.getPlottedNodes()) {
			drawNode(x, 0);
		}
		for(GraphNode x : pltr.getMovingNodes()) {
			drawNode(x, 127);
		}
		//ellipse(width/2, height/2, 100, 100);
		System.out.print("drawing completed.                                 \r");
	}

	private void drawNode(GraphNode x, int color) {
		stroke(color);
		//draw the node + every link
		ellipse((float) (width/2.0 + x.getxPos()*drawRootSize), 
				(float) (height/2.0 + x.getyPos()*drawRootSize),
				(float) (x.getRadius()*drawRootSize*2.0),
				(float) (x.getRadius()*drawRootSize*2.0));
		//System.out.println(x);
		for(GraphNode y : x.getChildren()) {
			if(!drawLines) break;
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
			vNorm1.mult((float)(x.getRadius()*drawRootSize));
			v1.sub(vNorm1);
			vNorm2.mult((float)(y.getRadius()*drawRootSize));
			v2.add(vNorm2);
			line(v1.x, v1.y, v2.x, v2.y);
		}
	}
}
