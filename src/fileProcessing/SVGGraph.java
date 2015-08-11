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
			append = append.replaceAll("%width", df.format(width*1024));
			append = append.replaceAll("%height", df.format(height*1024));
			append = append.replaceAll("%cornerx", df.format(posxmin*1024));
			append = append.replaceAll("%cornery", df.format(posymin*1024));
			
			writer.append(append);
			
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
					GraphNode it = x;
					int level = 255;
					while(it.getParent()!=null) {
						it = it.getParent();
						level -= 10;
					}
					if(level < 17) level = 17;
					String insert = "\t<circle cx=\"%cx\" cy=\"%cy\" r=\"%r\" "
							+ "stroke=\"none\" stroke-width=\"10px\" fill=\"%color\"/>\n";
					insert = insert.replaceAll("%cx", df.format(x.getxPos()*1024));
					insert = insert.replaceAll("%cy", df.format(x.getyPos()*1024));
					insert = insert.replaceAll("%r", df.format(x.getRadius()*1024));
					insert = insert.replaceAll("%color", "#" + Integer.toHexString(level) + "0000");
					writer.append(insert);
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
