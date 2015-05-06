import graph.GraphNode;
import plot.GraphPlotter;
import processing.core.*;

@SuppressWarnings("serial")
public class GraphDraw extends PApplet {
	private GraphPlotter pltr;
	
	public void setup() {
		size(200,200);
		background(0);

		pltr = new GraphPlotter();
	}

	public void draw() {
		GraphNode root = pltr.update();
		stroke(255);
		if (mousePressed) {
			line(mouseX,mouseY,pmouseX,pmouseY);
		}
	}
}
