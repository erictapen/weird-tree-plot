package fileProcessing;

import graph.GraphNode;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringEscapeUtils;

import plot.NodeSetManager;

/** Export feature for svg vector graphics. Export options are explained at the setters documentations.
 *
 * @author justin
 *
 */
public class SVGGraph {
	private double SCALE;
	private boolean writeCaption;
	private boolean writeCircles;
	private boolean writeEdges;
	private double strokeWidth; //relative to scale

	//TODO let this four variables make sense.
	private boolean plottable;
	private double bigcaption_minsize;
	private double smallcaption_minsize;
	private double circle_minsize;
	private double costXYratio;

	private ArrayList<GraphNode> bigCaptionNodes;
	private ArrayList<GraphNode> smallCaptionNodes;
	private ArrayList<GraphNode> noCaptionNodes;
	private ArrayList<GraphNode> noCircleNodes;

	private DecimalFormat df;
	
	public SVGGraph() {
		this.SCALE = 256.0;
		this.writeCaption = true;
		this.writeCircles = true;
		this.writeEdges = false;
		this.strokeWidth = 0.005;
		this.plottable = false;
		this.bigcaption_minsize = 0.05;
		this.smallcaption_minsize = 0.01;
		this.circle_minsize = 0.001;
		this.costXYratio = 1.0;
		Locale.setDefault(Locale.ENGLISH); //this is for proper working of DecimalFormat
		this.df = new DecimalFormat("#.########");
	}

	public void exportToSVG(String filename, HashSet<GraphNode> nodes) {

		try{
			FileWriter writer = new FileWriter(filename);
			this.appendSVGHeader(writer, nodes);
			if(this.plottable) {
				this.loadSeperateNodeClasses(nodes);
				
			} else {
				for(GraphNode x : nodes) {
					if(this.writeCaption && x.getRadius() > this.bigcaption_minsize) {
						this.appendCaption(writer, x);
					}
					if(this.writeCircles) {
						this.appendCircle(writer, x);
					}
					if(this.writeEdges) {
						this.appendEdge(writer, x);
					}
				}
			}
			writer.append("\n</svg>");

			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/** Gets the dimensions of the drawing and generates an SVG header from it.
	 * @param writer
	 * @param nodes
	 * @throws IOException
	 */
	private void appendSVGHeader(FileWriter writer, HashSet<GraphNode> nodes) throws IOException {
		double posxmin = Double.MAX_VALUE;
		double posxmax = Double.MIN_VALUE;
		double posymin = Double.MAX_VALUE;
		double posymax = Double.MIN_VALUE;
		for(GraphNode x : nodes) {
			if(x.getxPos() - x.getRadius() < posxmin) posxmin = x.getxPos() - x.getRadius();
			if(x.getxPos() + x.getRadius() > posxmax) posxmax = x.getxPos() + x.getRadius();
			if(x.getyPos() - x.getRadius() < posymin) posymin = x.getyPos() - x.getRadius();
			if(x.getyPos() + x.getRadius() > posymax) posymax = x.getyPos() + x.getRadius();
		}

		double width = posxmax - posxmin;
		double height = posymax - posymin;
		
		String append = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" "
				+ "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" +
				"\n" +
				"<svg xmlns=\"http://www.w3.org/2000/svg\"\n" +
				"     xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
				+ "xmlns:ev=\"http://www.w3.org/2001/xml-events\"\n" +
				"     version=\"1.1\" baseProfile=\"full\"\n" +
				"     width=\"%widthpx\" height=\"%heightpx\"\n" +
				"     viewBox=\"%cornerx %cornery %width %height\">\n" +
				"\n";
		append = append.replaceAll("%width", df.format(width*SCALE));
		append = append.replaceAll("%height", df.format(height*SCALE));
		append = append.replaceAll("%cornerx", df.format(posxmin*SCALE));
		append = append.replaceAll("%cornery", df.format(posymin*SCALE));

		writer.append(append);
	}
	
	private void appendCaption(FileWriter writer, GraphNode x) throws IOException {
		String insert = "<text x=\"%x\" y=\"%y\" textLength=\"%textlength\" " +
				"lengthAdjust=\"spacingAndGlyphs\"\n" +
				"      style=\"text-anchor: middle; font-size: %fontsizepx;\">\n" +
				"    %caption\n" +
				"</text>\n";
		insert = insert.replaceAll("%x", df.format(x.getxPos()*SCALE));
		insert = insert.replaceAll("%y", df.format(x.getyPos()*SCALE + x.getRadius()*SCALE*0.175));
		insert = insert.replaceAll("%textlength", df.format(x.getRadius()*1.8*SCALE));
		insert = insert.replaceAll("%fontsize", df.format(x.getRadius()*SCALE*0.5));
		insert = insert.replaceAll("%caption",
				StringEscapeUtils.escapeXml11(Matcher.quoteReplacement(x.getCaption())));
		writer.append(insert);
	}
	
	private void appendCircle(FileWriter writer, GraphNode x) throws IOException {
		String insert = "\t<circle cx=\"%cx\" cy=\"%cy\" r=\"%r\" "
				+ "stroke=\"%Stroke\" stroke-width=\"%strokeWidthpx\" fill=\"%color\"/>\n";
		insert = insert.replaceAll("%color", "none");
		insert = insert.replaceAll("%cx", df.format(x.getxPos()*SCALE));
		insert = insert.replaceAll("%cy", df.format(x.getyPos()*SCALE));
		insert = insert.replaceAll("%r", df.format(x.getRadius()*SCALE));
		insert = insert.replaceAll("%strokeWidth", df.format(strokeWidth*SCALE));
		insert = insert.replaceAll("%Stroke", "black");
		writer.append(insert);
	}
	
	private void appendEdge(FileWriter writer, GraphNode x) throws IOException {
		GraphNode p = x.getParent();
		if(p==null) return;
		String insert = "\t<line x1=\"%x1\" y1=\"%y1\" \n" +
				"          x2=\"%x2\" y2=\"%y2\" \n" +
				"          stroke=\"black\" \n" +
				"          stroke-width=\"%strokeWidth\"/>\n";
		insert = insert.replaceAll("%x1", df.format(x.getxPos()*SCALE));
		insert = insert.replaceAll("%y1", df.format(x.getyPos()*SCALE));
		insert = insert.replaceAll("%x2", df.format(p.getxPos()*SCALE));
		insert = insert.replaceAll("%y2", df.format(p.getyPos()*SCALE));
		insert = insert.replaceAll("%strokeWidth", df.format(strokeWidth*SCALE));
		writer.append(insert);
	}

	/** Fill every node in a it's set, according to its size.
	 * @param nodes
	 */
	private void loadSeperateNodeClasses(HashSet<GraphNode> nodes) {
		this.bigCaptionNodes = new ArrayList<GraphNode>();
		this.smallCaptionNodes = new ArrayList<GraphNode>();
		this.noCaptionNodes = new ArrayList<GraphNode>();
		this.noCircleNodes = new ArrayList<GraphNode>();
		for(GraphNode x : nodes) {
			if(x.getRadius() >= this.bigcaption_minsize) this.bigCaptionNodes.add(x);
			else if(x.getRadius() >= this.smallcaption_minsize) this.smallCaptionNodes.add(x);
			else if(x.getRadius() >= this.circle_minsize) this.noCaptionNodes.add(x);
			else this.noCircleNodes.add(x);
		}
	}

	/** Do Nearest Neighbour over every Nodeclass. Faster/better implementations may follow. Please note,
	 * that this will use a Manhattan-Metric, as plotter usually can move the pen on both axis!
	 * @param ratio Cost x / cost y for physical plotter, where one axis moves faster than another.
	 */
	private void sortNodeClassesTSP(double ratio) {
		NodeSetManager mngr = new NodeSetManager();
		mngr.init();
		//TODO
	}

	/** Check this to draw text. (true)
	 * @param writeCaption
	 */
	public void setWriteCaption(boolean writeCaption) {
		this.writeCaption = writeCaption;
	}

	/** Check this to draw circles. Imho setting this to false is only good for debugging. (true)
	 * @param writeCircles
	 */
	public void setWriteCircles(boolean writeCircles) {
		this.writeCircles = writeCircles;
	}

	/** Edges will be drawn. In terms of clearness its more or less a shitty idea to activate this.
	 * But it looks good! (false)
	 * @param writeEdges
	 */
	public void setWriteEdges(boolean writeEdges) {
		this.writeEdges = writeEdges;
	}

	/** Set this true, if you want your SVG to be plottable with a physical plotter. The drawing will
	 * be optimized in the following way:
	 * * Travelling Salesman solution over all nodes, to accomplish faster plotting. If your
	 * plotter has different costs at the x and y-Axis, set your ratio with setCostXYratio()
	 * * Nodes which are smaller than caption_minsize will be drawn without text
	 * * Nodes which are smaller than circle_minsize will be drawn as a single line, which treis to connect
	 * as many small nodes as possible. Think of it as a random line which draws areas black which would
	 * otherwise be filled with very small circles.
	 */
	public void setPlottable(boolean plottable) {
		this.plottable = plottable;
	}

	/** If plottable is set, only nodes which are bigger or equal than bigcaption_minsize will be drawn
	 * with double-lined text.
	 * (0.1)
	 * @param caption_minsize
	 */
	public void setBigCaption_minsize(double bigcaption_minsize) {
		this.bigcaption_minsize = bigcaption_minsize;
	}

	/** If plottable is set, only nodes which are bigger or equal than smallcaption_minsize will be drawn
	 * with thin and easy drawable text.
	 * (0.1)
	 * @param caption_minsize
	 */
	public void setSmallCaption_minsize(double smallcaption_minsize) {
		this.smallcaption_minsize = smallcaption_minsize;
	}

	/** If plottable is set, nodes which are smaller than circle_minsize will be drawn as a single line,
	 * which tries to connect as many small nodes as possible. Think of it as a random line which draws
	 * areas black which would otherwise be filled with very small circles. (0.001)
	 * @param circle_minsize
	 */
	public void setCircle_minsize(double circle_minsize) {
		this.circle_minsize = circle_minsize;
	}

	/** If plottable is set, you can have a Travelling Salesman solution over all nodes, to accomplish
	 * faster plotting. If your plotter has different costs at the x and y-Axis, set your ratio here.
	 * If your plotter needs for 1m in x-direction twice as much than for 1m in y-direction, set it
	 * to 2.0. (1.0)
	 * @param cost_xy_ratio
	 */
	public void setCostXYratio(double cost_xy_ratio) {
		this.costXYratio = cost_xy_ratio;
	}


}
