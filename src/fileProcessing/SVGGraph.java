package fileProcessing;

import graph.GraphNode;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Locale;

/** Export feature for svg vector graphics
 * At the moment, only circles will be written
 * @author justin
 *
 */
public class SVGGraph {

	public static void exportToSVG(	String filename, HashSet<GraphNode> nodes, boolean writeCaption,
									boolean writeCircles, boolean writeEdges) {
		
		writeCaption = false;
		writeEdges = false;
		double posxmin = 0.0;
		double posxmax = 0.0;
		double posymin = 0.0;
		double posymax = 0.0;
		for(GraphNode x : nodes) {
			if(x.getxPos() - x.getRadius() < posxmin) posxmin = x.getxPos() - x.getRadius();
			if(x.getxPos() + x.getRadius() > posxmax) posxmax = x.getxPos() + x.getRadius();
			if(x.getyPos() - x.getRadius() < posymin) posymin = x.getyPos() - x.getRadius();
			if(x.getyPos() + x.getRadius() > posymax) posymax = x.getyPos() + x.getRadius();
		}
		
		Locale.setDefault(Locale.ENGLISH);
		DecimalFormat df = new DecimalFormat("#.########");
		try{
			FileWriter writer = new FileWriter(filename);

			writer.append(
					"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
					"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\" "
					+ "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n" + 
					"\n" + 
					"<svg xmlns=\"http://www.w3.org/2000/svg\"\n" + 
					"     xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
					+ "xmlns:ev=\"http://www.w3.org/2001/xml-events\"\n" + 
					"     version=\"1.1\" baseProfile=\"full\"\n" + 
					"     width=\"1024mm\" height=\"1024mm\"\n" + 
					"     viewBox=\"-512 -512 1024 1024\">\n" + 
					"\n"
					
					
							
					);
			for(GraphNode x : nodes) {
				if(writeCaption) {
					double size = (50.0*x.getRadius())/(double)x.getCaption().length();
					if(size!=0.0) {
						writer.append("\\node[scale=" + 
								df.format(size) + 
								"pt] at (" + 
								df.format(x.getxPos()) + "*\\SCALE," + 
								df.format(x.getyPos()) + "*\\SCALE) {" + x.getCaption() + "};\n"); 
					}
				}
				if(writeCircles) {
					String insert = "<circle cx=\"%cx\" cy=\"%cy\" r=\"%r\"/>";
					insert.replaceAll("%cx", df.format(x.getxPos()));
					insert.replaceAll("%cy", df.format(x.getyPos()));
					insert.replaceAll("%r", df.format(x.getRadius()));
					
					
					writer.append("<circle cx=\"%cx\" cy=\"%cy\" r=\"%r\"/>");
				}
				try{
					GraphNode p = x.getParent();
					if(writeEdges && p!=null) {
						writer.append("\\draw [->] (" + 
								df.format(x.getxPos()) + "*\\SCALE," + 
								df.format(x.getyPos()) + "*\\SCALE) -- (" + 
								df.format(p.getxPos()) + "*\\SCALE," + 
								df.format(p.getyPos()) + "*\\SCALE);\n\n");
					}
				} catch(NullPointerException e) {}


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
}
