package fileProcessing;

import graph.GraphNode;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringEscapeUtils;

/** Export feature for svg vector graphics. Export options are explained at the setters documentations.
 * 
 * @author justin
 *
 */
public class SVGGraph {
	
	
	
	
	
	
	private boolean writeCaption;
	private boolean writeCircles;
	private boolean writeEdges;
	
	//TODO let this four variables make sense.
	private boolean plottable;
	private double caption_minsize;
	private double circle_minsize;
	private double costXYratio;
	
	public SVGGraph() {
		this.writeCaption = true;
		this.writeCircles = true;
		this.writeEdges = false;
		this.plottable = false;
		this.caption_minsize = 0.01;
		this.circle_minsize = 0.001;
		this.costXYratio = 1.0;
	}

	public void exportToSVG(	String filename, HashSet<GraphNode> nodes) {
		double scale = 256.0;
		double strokeWidth = 0.005; //relative to scale
		boolean fillGradient = false;
		boolean stroke = true;
		double captionMinNodesize = 0.01;
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
		
		
		Locale.setDefault(Locale.ENGLISH);
		DecimalFormat df = new DecimalFormat("#.########");
		try{
			FileWriter writer = new FileWriter(filename);
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
			append = append.replaceAll("%width", df.format(width*scale));
			append = append.replaceAll("%height", df.format(height*scale));
			append = append.replaceAll("%cornerx", df.format(posxmin*scale));
			append = append.replaceAll("%cornery", df.format(posymin*scale));
			
			writer.append(append);
			
			for(GraphNode x : nodes) {
				if(this.writeCaption && x.getRadius() > captionMinNodesize) {
					String insert = "<text x=\"%x\" y=\"%y\" textLength=\"%textlength\" " +
							"lengthAdjust=\"spacingAndGlyphs\"\n" + 
							"      style=\"text-anchor: middle; font-size: %fontsizepx;\">\n" + 
							"    %caption\n" + 
							"</text>\n";
					insert = insert.replaceAll("%x", df.format(x.getxPos()*scale));
					insert = insert.replaceAll("%y", df.format(x.getyPos()*scale + x.getRadius()*scale*0.175));
					insert = insert.replaceAll("%textlength", df.format(x.getRadius()*1.8*scale));
					insert = insert.replaceAll("%fontsize", df.format(x.getRadius()*scale*0.5));
					insert = insert.replaceAll("%caption",  
							StringEscapeUtils.escapeXml11(Matcher.quoteReplacement(x.getCaption())));
					writer.append(insert);
				}
				if(this.writeCircles) {
					GraphNode it = x;
					String insert = "\t<circle cx=\"%cx\" cy=\"%cy\" r=\"%r\" "
							+ "stroke=\"%Stroke\" stroke-width=\"%strokeWidthpx\" fill=\"%color\"/>\n";
					if(fillGradient) {
						int level = 255;
						while(it.getParent()!=null) {
							it = it.getParent();
							level -= 30;
						}
						if(level < 17) level = 17;
						insert = insert.replaceAll("%color", "#" + 
								Integer.toHexString(level) + "0000");
					} else {
						insert = insert.replaceAll("%color", "none");
					}
					insert = insert.replaceAll("%cx", df.format(x.getxPos()*scale));
					insert = insert.replaceAll("%cy", df.format(x.getyPos()*scale));
					insert = insert.replaceAll("%r", df.format(x.getRadius()*scale));
					insert = insert.replaceAll("%strokeWidth", df.format(strokeWidth*scale));
					if(stroke) insert = insert.replaceAll("%Stroke", "black");
					else insert = insert.replaceAll("%Stroke", "none");
					writer.append(insert);
				}
				GraphNode p = x.getParent();
				if(this.writeEdges && p!=null) {
					String insert = "\t<line x1=\"%x1\" y1=\"%y1\" \n" + 
							"          x2=\"%x2\" y2=\"%y2\" \n" + 
							"          stroke=\"black\" \n" + 
							"          stroke-width=\"%strokeWidth\"/>\n";
					insert = insert.replaceAll("%x1", df.format(x.getxPos()*scale));
					insert = insert.replaceAll("%y1", df.format(x.getyPos()*scale));
					insert = insert.replaceAll("%x2", df.format(p.getxPos()*scale));
					insert = insert.replaceAll("%y2", df.format(p.getyPos()*scale));
					insert = insert.replaceAll("%strokeWidth", df.format(strokeWidth*scale));
					writer.append(insert);
				}
			}
			writer.append(
					"\\n\" + \n" + 
					"					\"</svg>"
					);

			writer.flush();
			writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
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

	/** If plottable is set, nodes which are smaller than caption_minsize will be drawn without text. 
	 * (0.1)
	 * @param caption_minsize
	 */
	public void setCaption_minsize(double caption_minsize) {
		this.caption_minsize = caption_minsize;
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
