package fileProcessing;

import graph.GraphNode;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Locale;

public class TexGraph {

	private static final double MAX_SCALE = 5.0;

	public static void exportToTex(	String filename, HashSet<GraphNode> nodes, boolean writeCaption,
									boolean writeCircles, boolean writeEdges) {
		Locale.setDefault(Locale.ENGLISH);
		DecimalFormat df = new DecimalFormat("#.######");
		try{
			FileWriter writer = new FileWriter(filename);

			writer.append(
					"\\documentclass{standalone}\n" +
							"\\usepackage{tikz}\n" +
							"\\pagestyle{empty}\n" +
							"\\usepackage[english,ngerman]{babel}\n" +
							"\\usepackage[utf8]{inputenc}\n" +
							"\\newcommand{\\SCALE}{" + df.format(MAX_SCALE) + "}\n" +
							"\\begin{document}\n" +
							"\\begin{tikzpicture}\n"
							
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
				/*if(writeBoxes) {
					writer.append("\\node (rect) at (" + 
							df.format(x.getPosX()*MAX_SCALE) + "," + 
							df.format(x.getPosY()*MAX_SCALE) + ") [draw, minimum width=" + 
							df.format(x.getWidth()*MAX_SCALE) + "cm, minimum height=" + 
							df.format(x.getHeight()*MAX_SCALE) + "cm] {};\n"); 

				}*/
				if(writeCircles) {
					writer.append("\\draw (" + 
							df.format(x.getxPos()) + "*\\SCALE, " + 
							df.format(x.getyPos()) + "*\\SCALE) circle (" + 
							df.format(x.getRadius()) + "*\\SCALE );\n");
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
					"\\end{tikzpicture}\n" +
							"\\end{document}\n"
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
