import fileProcessing.ConfReader;
import fileProcessing.SortedGraph;
import fileProcessing.TexGraph;
import graph.GraphNode;
import plot.GraphPlotter;
import processing.core.*;

@SuppressWarnings("serial")
public class GraphDraw extends PApplet {
	PApplet parent; // The parent PApplet that we will render ourselves onto

	private GraphNode root;
	/** The size of the root node on the sreen, in pixels
	 */
	private double drawRootSize;
	/**check this, if you want to draw edges of the graph
	 */
	private boolean drawLines;
	/** How often to update the Plotter, before the picture is redrawn.
	 */
	private int drawEveryUpdateInterval;
	/** Where to export the final TEX file
	 */
	private String exportfile;
	/**Where to get the input DOT file
	 */
	private String inputDOTfile;
	/** Which Article should stand in the middle?
	 */
	private String rootCaption;
	private ConfReader config;
	private GraphPlotter pltr;

	private boolean exportAndClose = false;
	
	public void setup() {
		int sizex = 0;
		int sizey = 0;
		
		background(0xffffff);
		noFill();
		
		
		config = new ConfReader("../plotter.conf");
		String value;
		
		value = config.getValueByKey("DRAWdisplayWidth");
		try {
			if(value != null) sizex = Integer.parseInt(value);
			else sizex = 512; //default value
		} catch (NumberFormatException e) {
			System.out.print("Config Syntax Error. " + value + " is not an appropiate value for"
					+ "DRAWdisplayWidth.");
		}
		
		value = config.getValueByKey("DRAWdisplayHeight");
		try {
			if(value != null) sizey = Integer.parseInt(value);
			else sizey = 0; //default value
		} catch (NumberFormatException e) {
			System.out.print("Config Syntax Error. " + value + " is not an appropiate value for"
					+ "DRAWdisplayHeight.");
		}
		
		if(sizey==0) size(sizex, sizex);
		else size(sizex, sizey);
		
		value = config.getValueByKey("GRAPHinputDOTfile");
		if(value != null) this.inputDOTfile = value;
		else this.inputDOTfile = 
				"../data/wiki_sorted_attr.dot"; //default value
		
		value = config.getValueByKey("GRAPHrootCaption");
		if(value != null) this.rootCaption = value;
		else this.rootCaption = "formale Sprache"; //default value
		
		value = config.getValueByKey("GRAPHoutputTEXfile");
		if(value != null) this.exportfile = value;
		else this.exportfile = "../out/out.tex"; //default value
		
		value = config.getValueByKey("DRAWrootSize");
		try {
			if(value != null) this.drawRootSize = Double.parseDouble(value);
			else this.drawRootSize = 75.0; //default value
		} catch (NumberFormatException e) {
			System.out.print("Config Syntax Error. " + value + " is not an appropiate value for"
					+ "DRAWrootSize.");
		}
		
		value = config.getValueByKey("DRAWlines");
		if(value == "true") this.drawLines = true;
		else if(value == "false") this.drawLines = false;
		else this.drawLines = false; //default value
		
		value = config.getValueByKey("DRAWeveryNumberOfUpdates");
		try {
			if(value != null) this.drawEveryUpdateInterval = Integer.parseInt(value);
			else this.drawEveryUpdateInterval = 10; //default value
		} catch (NumberFormatException e) {
			System.out.print("Config Syntax Error. " + value + " is not an appropiate value for"
					+ "DRAWeveryNumberOfUpdates.");
		}
		
		root = SortedGraph.importFile(this.inputDOTfile, this.rootCaption);
		
		if(root == null) {
			System.out.println("root is null! You have to tell the program where it should start."
					+ "Plese check your config again. "
					+ "The file at GRAPHinputDOTfile must include a Node GRAPHrootCaption."
					+ "\nWill terminate.");
			exit();
		}
		System.out.println("rootnode is: " + root.getCaption());
		pltr = new GraphPlotter(root, true);
		pltr.init(config);
		pltr.getManager().init(config);
		
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
