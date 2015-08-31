package draw;
import java.util.Vector;

import fileProcessing.ConfReader;
import fileProcessing.SVGGraph;
import fileProcessing.SortedGraph;
import fileProcessing.TexGraph;
import graph.GraphNode;
import plot.GraphPlotter;
import processing.core.*;

@SuppressWarnings("serial")
public class GraphDraw extends PApplet {
	PApplet parent; // The parent PApplet that we will render ourselves onto

	private GraphNode root;
	/** The size of the root node on the screen, in pixels
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
	
	/** This is for benchmarking
	 * 
	 */
	private long startTime = 0;
	private long stopTime = 0;

	private boolean drawEverything = false;
	
	public void setup() {
		startTime = System.currentTimeMillis();
		
		background(0xffffff);
		noFill();
		System.out.println(System.getProperty("user.dir"));
		
		config = new ConfReader();
		config.loadConfFromFile("plotter.conf");
		config.loadConfFromCMDArguments(args);
		config.setupGraphDraw(this);
				
		
		
		
		root = SortedGraph.importFile(this.inputDOTfile, this.rootCaption);
		
		if(root == null) {
			System.out.println("root is null! You have to tell the program where it should start."
					+ "Plese check your config again. "
					+ "The file at GRAPHinputDOTfile must include a Node GRAPHrootCaption."
					+ "\nWill terminate.");
			exit();
		}
		if(root.getRadius() == 1.0) abortAndExport(); //Graph is already plotted
		System.out.println("rootnode is: " + root.getCaption());
		pltr = new GraphPlotter(root, true);
		pltr.init(config);
		pltr.getManager().init(config);
		
		System.out.println("root has " + root.getChildren().size() + " children.");
		SortedGraph.exportFile(this.root, "data/wiki_attr_before_plot.dot", true, pltr.getMinNodeLeafs());
	}

	public void draw() {
		background(0xFFFFFF);
		System.out.print(	"Plot in progress: " + pltr.getIteration() + "/" + pltr.getMaxIteration() 
							+ "iterations, \n" + pltr.getMovingNodes().size() + " movingNodes, "
							+ pltr.getPlottedNodes().size() + " plottedNodes and " 
							+ pltr.getWaitingNodes().size() + " waitingNodes\n");
		//System.out.println(pltr.getManager().getStatus());
		for(int i=0; i<drawEveryUpdateInterval; i++) {
			pltr.update();
		}
		System.out.print("Start drawing.                                    \n");
		//draws a grid of 1*1 units for better view
		drawOneUnitGrid();
		//highlights every block of the NodeSetManager for better optimization
		drawNodeSetManagerGrid();
		drawWaitingNodeSetManagerGrid();
		if(drawEverything ) {
			//draw Nodes
			for(GraphNode x : pltr.getPlottedNodes()) {
				drawNode(x, 0);
			}
			for(GraphNode x : pltr.getMovingNodes()) {
				drawNode(x, 127);
			}
		}
		
		System.out.print("drawing completed.                                 \n");
		if(pltr.getWaitingNodes().isEmpty() || exportAndClose) {
			abortAndExport();
		}
	}


	

	private void abortAndExport() {
		System.out.println("Starting export to tikz, svg, dot.");
		if(pltr!=null) {
			TexGraph.exportToTex(	exportfile, 
									pltr.getPlottedNodes(), true, true, false);
			SVGGraph.exportToSVG(	exportfile.replaceAll(".tex", ".svg"), 
					pltr.getPlottedNodes(), false, true, false);
			SortedGraph.exportFile(pltr.getRoot(), exportfile.replaceAll(".tex", ".dot"), true);
		} else {
			TexGraph.exportToTex(	exportfile, 
					this.root.getWholeTree(), true, true, false);
			SVGGraph.exportToSVG(	exportfile.replaceAll(".tex", ".svg"), 
					this.root.getWholeTree(), false, true, false);
			SortedGraph.exportFile(this.root, exportfile.replaceAll(".tex", ".dot"), true);
		}
		
		System.out.println("Export to tikz, svg, dot complete.");
		stopTime = System.currentTimeMillis();
		System.out.println("Program ran in " + (stopTime - startTime) + " Milliseconds.");
		saveFrame("/home/justin/git/wikipedia-map/out/screen.png");
		exit();
	}

	private void drawNode(GraphNode x, int color) {
		noFill();
		stroke(color);
		if(!x.isPlotted()) {
			stroke(255, 0, 255);
			fill(255, 0, 255);
		} //TODO
		
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
	

	/** draws a grid of 1*1 units for better view
	 * 
	 */
	private void drawOneUnitGrid() {
		stroke(0);
		for(double i=width/2.0; i>0; i -= drawRootSize) {
			line((int)i, 0, (int)i, height);
		}
		for(double i=width/2.0; i<width; i += drawRootSize) {
			line((int)i, 0, (int)i, height);
		}
		for(double i=height/2.0; i>0; i -= drawRootSize) {
			line(0, (int)i, width, (int)i);
		}
		for(double i=height/2.0; i<height; i += drawRootSize) {
			line(0, (int)i, width, (int)i);
		}
	}
	
	/** highlights every block of the NodeSetManager for better optimization
	 * 
	 */
	private void drawNodeSetManagerGrid() {
		float x1;
		float y1;
		float x2;
		float y2;
		for(Vector<Integer> x : this.pltr.getManager().getOverview()) {
			x1 = (float)(x.elementAt(0)*this.pltr.getManager().getGridsize()*this.drawRootSize);
			x1 += width/2.0;
			y1 = (float)(x.elementAt(1)*this.pltr.getManager().getGridsize()*this.drawRootSize);
			y1 += height/2.0;
			x2 = (float)(this.pltr.getManager().getGridsize()*this.drawRootSize);
			y2 = (float)(this.pltr.getManager().getGridsize()*this.drawRootSize);
			int fill = (int)(x.get(2));
			if(fill<=20) fill = 20;
			fill(fill);
			stroke(fill);
			rect(x1, y1, x2, y2);
		}
	}
	
	/** highlights every block of the WaitingNodeSetManager for better optimization
	 * 
	 */
	private void drawWaitingNodeSetManagerGrid() {
		float x1;
		float y1;
		float x2;
		float y2;
		for(Vector<Integer> x : this.pltr.getMovingmanager().getOverview()) {
			x1 = (float)(x.elementAt(0)*this.pltr.getManager().getGridsize()*this.drawRootSize);
			x1 += width/2.0;
			y1 = (float)(x.elementAt(1)*this.pltr.getManager().getGridsize()*this.drawRootSize);
			y1 += height/2.0;
			x2 = (float)(this.pltr.getManager().getGridsize()*this.drawRootSize);
			y2 = (float)(this.pltr.getManager().getGridsize()*this.drawRootSize);
			int fill = (int)(x.get(2));
			if(fill<=20) fill = 20;
			fill(fill, 0, 0);
			stroke(fill);
			rect(x1, y1, x2, y2);
		}
	}


	public void mousePressed() {
		   exportAndClose = true;
		   System.out.println("Will abort program asap.");
	}

	public void setDrawRootSize(double drawRootSize) {
		this.drawRootSize = drawRootSize;
	}

	public void setDrawLines(boolean drawLines) {
		this.drawLines = drawLines;
	}

	public void setDrawEveryUpdateInterval(int drawEveryUpdateInterval) {
		this.drawEveryUpdateInterval = drawEveryUpdateInterval;
	}

	public void setExportfile(String exportfile) {
		this.exportfile = exportfile;
	}

	public void setInputDOTfile(String inputDOTfile) {
		this.inputDOTfile = inputDOTfile;
	}

	public void setRootCaption(String rootCaption) {
		this.rootCaption = rootCaption;
	}

	public void setDrawEverything(boolean drawEverything) {
		this.drawEverything = drawEverything;
	}
	
	

}
