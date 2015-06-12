import fileProcessing.SortedGraph;
import fileProcessing.TexGraph;
import graph.GraphNode;
import plot.GraphPlotter;
import processing.core.*;

@SuppressWarnings("serial")
public class GraphDraw extends PApplet {
	PApplet parent; // The parent PApplet that we will render ourselves onto


	private GraphPlotter pltr;
	private GraphNode root;
	private double drawRootSize = 75.0;
	private boolean drawLines = false;
	private int drawEveryUpdateInterval = 10;
	private boolean exportAndClose = false;
	private String exportfile = "/home/justin/git/wikipedia-map/out/test.tex";

	
	public void setup() {
		
		size(1024, 1024);
		background(0xffffff);
		noFill();

		root = SortedGraph.importFile(
				"/home/justin/git/wikipedia-map/data/wiki_sorted.dot", 
				"Wissenschaft");
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

		if(root == null) {
			System.out.println("root is null! You have to tell the program where it have to start."
					+ "\nWill terminate.");
			exit();
		}
		System.out.println("rootnode is: " + root.getCaption());
		pltr = new GraphPlotter(root, true);
		pltr.setRedrawInterval(200);
		pltr.setMaxIteration(2000);
		pltr.setStepsize(0.01);
		pltr.setMovingCircleRadius(10.0);
		pltr.setSizeOffSet(100000.0);
		pltr.setMinNodeLeafs(1000);
		pltr.getManager().setGridsize(0.0125);
		pltr.setMinStepSizeBeforeAbort(0.02);
		pltr.setPersistenceBeforeAbort(500);
		System.out.println("root has " + root.getChildren().size() + " children.");
	}

	public void draw() {
		if(pltr.getWaitingNodes().isEmpty() || exportAndClose) {
			System.out.println("Starting export to tikz.");
			TexGraph.exportToTex(	exportfile, 
									pltr.getPlottedNodes(), true, true, false);
			System.out.println("Export to tikz complete.");
			exit();
		}
		background(0xFFFFFF);
		System.out.print(	"Plot in progress: " + pltr.getIteration() + "/" + pltr.getMaxIteration() 
							+ "iterations, \n" + pltr.getMovingNodes().size() + " movingNodes, "
							+ pltr.getPlottedNodes().size() + " plottedNodes and " 
							+ pltr.getWaitingNodes().size() + " waitingNodes\n");
		for(int i=0; i<drawEveryUpdateInterval; i++) {
			pltr.update();
		}
		System.out.print("Start drawing.                                    \n");
		for(GraphNode x : pltr.getPlottedNodes()) {
			drawNode(x, 0);
		}
		for(GraphNode x : pltr.getMovingNodes()) {
			drawNode(x, 127);
		}
		//ellipse(width/2, height/2, 100, 100);
		System.out.print("drawing completed.                                 \n");
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
		ellipse(width/2,
				height/2,
				(float) (pltr.getMovingCircleRadius()*0.5*drawRootSize),
				(float) (pltr.getMovingCircleRadius()*0.5*drawRootSize));
	}


	public void mousePressed() {
		   exportAndClose = true;
		   System.out.println("Will abort program asap.");
		}

}
