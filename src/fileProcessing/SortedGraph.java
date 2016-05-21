package fileProcessing;

import graph.GraphNode;
import monitor.ImportM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Import and Export functionality for .dot-files which are already sorted and
 * are therefore faster readable. There are only .dot-files with a single graph
 * allowed! There is a lot of code just pasted from RawGraph.java
 * 
 * @author justin
 *
 */
public class SortedGraph {
	private static HashMap<String, GraphNode> nodemap = new HashMap<String, GraphNode>(4000000, (float) 0.75);

	/**
	 * import a single (!) TreeGraph (!) from file
	 * 
	 * @param ifile
	 *            file location
	 * @param rootcaption
	 *            caption of the desired rootNode
	 * @return the rootNode
	 */
	public static GraphNode importFile(String ifile, String rootcaption, ImportM mon) {
		mon.setRootcaption(rootcaption);
		mon.setImportfile(ifile);
		mon.setState(ImportM.State.IMPORTING);
		try (BufferedReader br = new BufferedReader(new FileReader(ifile))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				createNodeFromLine(line);
				i++;
				mon.setNodesImported(i);
			}
		} catch (FileNotFoundException e) {
			mon.setError(ImportM.Error.FILE_NOT_FOUND);
			return null;
		} catch (IOException e) {
			mon.setError(ImportM.Error.IO_EXCEPTION);
			return null;
		}
		mon.setUniqueNodesImported(nodemap.size());
		GraphNode root = nodemap.get(rootcaption);
		if (root == null)
			return null;
		boolean graphNeedsUpdateOnLeafSizes = false;
		boolean graphNeedsPlot = false;
		ArrayList<GraphNode> togo = new ArrayList<GraphNode>();
		ArrayList<GraphNode> togo2 = new ArrayList<GraphNode>();
		togo.add(root);
		while (!togo.isEmpty()) {
			for (GraphNode x : togo) {
				if (x.getTreeSize() == 0)
					graphNeedsUpdateOnLeafSizes = true;
				if (x.getRadius() == 0.0) {
					graphNeedsPlot = true;
				}
				togo2.addAll(x.getChildren());
			}
			togo.clear();
			togo.addAll(togo2);
			togo2.clear();
		}
		if (root.getRadius() != 1.0)
			graphNeedsPlot = true;
		if (graphNeedsUpdateOnLeafSizes) {
			mon.setState(ImportM.State.UPDATING_LEEF_SIZES);
			root.updateTreeSize();
		} else {
			mon.setState(ImportM.State.NOT_UPDATING_LEEF_SIZES);
		}
		if (graphNeedsPlot) {
			mon.setState(ImportM.State.GRAPH_IS_NOT_PLOTTED);
		} else {
			mon.setState(ImportM.State.GRAPH_IS_PLOTTED);
		}
		mon.setState(ImportM.State.COMPLETED);
		return root;
	}

	/**
	 * takes a line and adds the accounting relation into the whole graphset
	 * (ArrayList nodes)
	 * 
	 * @param line
	 */
	private static void createNodeFromLine(String line) {
		line = line.replace("\t", ""); // deletes the tab at the beginning
		String[] str = line.split(" <-- ");
		boolean parentGotAttr = false;
		boolean childGotAttr = false;
		int attrTreeSizeParent = 0;
		double attrPosXParent = 0.0;
		double attrPosYParent = 0.0;
		double attrRadiusParent = 0.0;
		String attrCaptionParent = null;
		if (str[0].contains("[")) {
			attrTreeSizeParent = extractAttributeFromString(str[0], "treeSize", 0);
			attrPosXParent = extractAttributeFromString(str[0], "posx", 0.0);
			attrPosYParent = extractAttributeFromString(str[0], "posy", 0.0);
			attrRadiusParent = extractAttributeFromString(str[0], "radius", 0.0);
			attrCaptionParent = extractAttributeFromString(str[0], "caption", null);
			parentGotAttr = true;
			str[0] = str[0].substring(0, str[0].indexOf(" ["));
		}
		if (attrCaptionParent == null)
			attrCaptionParent = str[0];

		int attrTreeSize = 0;
		double attrPosX = 0.0;
		double attrPosY = 0.0;
		double attrRadius = 0.0;
		String attrCaption = null;
		if (str.length != 2)
			return;
		if (str[1].contains("[")) { // Attributes are read out from string
			attrTreeSize = extractAttributeFromString(str[1], "treeSize", 0);
			attrPosX = extractAttributeFromString(str[1], "posx", 0.0);
			attrPosY = extractAttributeFromString(str[1], "posy", 0.0);
			attrRadius = extractAttributeFromString(str[1], "radius", 0.0);
			attrCaption = extractAttributeFromString(str[1], "caption", null);
			childGotAttr = true;
			str[1] = str[1].substring(0, str[1].indexOf(" ["));
		}
		if (attrCaption == null)
			attrCaption = str[1];

		GraphNode parent = nodemap.get(str[0]);
		GraphNode child = nodemap.get(str[1]);
		if (child == null) {
			child = new GraphNode(attrCaption);
			nodemap.put(str[1], child);
		}
		if (parent == null) {
			parent = new GraphNode(attrCaptionParent);
			nodemap.put(str[0], parent);
		}
		child.setParent(parent);
		parent.addChild(child);
		if (parentGotAttr) {
			if (attrTreeSizeParent != 0)
				parent.setTreeSize(attrTreeSizeParent);
			parent.setxPos(attrPosXParent);
			parent.setyPos(attrPosYParent);
			parent.setRadius(attrRadiusParent);
		}
		if (childGotAttr) {
			child.setTreeSize(attrTreeSize);
			child.setxPos(attrPosX);
			child.setyPos(attrPosY);
			child.setRadius(attrRadius);
		}
	}

	/**
	 * @param str
	 *            An attribute String
	 * @param key
	 *            The key of the desired attribute
	 * @return The desired double value, or defaultVal if no corresponding value
	 *         could be found
	 */
	private static Double extractAttributeFromString(String str, String key, double defaultVal) {
		String res = extractAttributeFromString(str, key);
		try {
			return Double.parseDouble(res);
		} catch (NumberFormatException e) {
			return defaultVal;
		} catch (NullPointerException e) {
			return defaultVal;
		}
	}

	/**
	 * @param str
	 *            An attribute String
	 * @param key
	 *            The key of the desired attribute
	 * @return The desired double value, or defaultVal if no corresponding value
	 *         could be found
	 */
	private static Integer extractAttributeFromString(String str, String key, int defaultVal) {
		String res = extractAttributeFromString(str, key);
		try {
			return Integer.parseInt(res);
		} catch (NumberFormatException e) {
			return defaultVal;
		} catch (NullPointerException e) {
			return defaultVal;
		}
	}

	/**
	 * @param str
	 *            An attribute String
	 * @param key
	 *            The key of the desired attribute
	 * @return The desired double value, or defaultVal if no corresponding value
	 *         could be found
	 */
	@SuppressWarnings("unused")
	private static Boolean extractAttributeFromString(String str, String key, boolean defaultVal) {
		String res = extractAttributeFromString(str, key);
		if (res == null)
			return defaultVal;
		if (res == "true")
			return new Boolean(true); // the Boolean.parseBoolean() function is
										// not suited.
		if (res == "false")
			return new Boolean(false);
		return defaultVal;
	}

	/**
	 * @param str
	 *            An attribute String
	 * @param key
	 *            The key of the desired attribute
	 * @return The desired double value, or defaultVal if no corresponding value
	 *         could be found
	 */
	private static String extractAttributeFromString(String str, String key, String defaultVal) {
		String res = extractAttributeFromString(str, key);
		if (res == null)
			return defaultVal;
		return res;
	}

	/**
	 * @param str
	 *            An attribute String
	 * @param key
	 *            The key of the desired attribute
	 * @return The desired double value, or defaultVal if no corresponding value
	 *         could be found
	 */
	private static String extractAttributeFromString(String str, String key) {
		Pattern pattern = Pattern.compile(key + "=\"(.+?)\"");
		Matcher matcher = pattern.matcher(str);
		matcher.find();
		try {
			return matcher.group(1);
		} catch (IllegalStateException e) {
			return null;
		}
	}

	/**
	 * Exports the graph! Every data, which is determined by now will be written
	 * into the file
	 * 
	 * @param root
	 *            The rootNode where to start. Every other node with posx=0.0,
	 *            posy=0.0 will be seen as without position data!
	 * @param ofile
	 *            Filename where to export. File must exist!
	 * @param minTreeSize
	 */
	public static void exportFile(GraphNode root, String ofile, boolean writeAttributes, int minTreeSize) {
		if (writeAttributes) {
			System.out.println("Starting sorted DOT export of " + root.getCaption() + " to " + ofile
					+ ". Attributes will be added.");
		} else {
			System.out.println("Starting sorted DOT export of " + root.getCaption() + " to " + ofile
					+ ". Attributes will be NOT added.");
		}
		try {
			FileWriter writer = new FileWriter(ofile);

			writer.append("digraph " + root.getCaption() + " {\n");
			ArrayList<GraphNode> togo = new ArrayList<GraphNode>();
			ArrayList<GraphNode> togo2 = new ArrayList<GraphNode>();
			togo.addAll(root.getChildren());
			while (!togo.isEmpty()) {
				for (GraphNode x : togo) {
					if (x.getTreeSize() < minTreeSize)
						continue; // Exporting only more relevant nodes, if
									// necessary
					writer.append("\t" + x.getParent().getCaption());
					if (writeAttributes && x.getParent() == root) {
						String append = " [caption=\"%caption\", " + "treeSize=\"%treeSize\", " + "posx=\"%posx\", "
								+ "posy=\"%posy\", " + "radius=\"%radius\"]";
						append = append.replaceAll("%caption", x.getParent().getCaption());
						append = append.replaceAll("%treeSize", Integer.toString(x.getParent().getTreeSize()));
						append = append.replaceAll("%posx", Double.toString(x.getParent().getxPos()));
						append = append.replaceAll("%posy", Double.toString(x.getParent().getyPos()));
						append = append.replaceAll("%radius", Double.toString(x.getParent().getRadius()));
						writer.append(append);
					}
					writer.append(" <-- " + x.getCaption());
					if (writeAttributes) {
						String append = " [caption=\"%caption\", " + "treeSize=\"%treeSize\", " + "posx=\"%posx\", "
								+ "posy=\"%posy\", " + "radius=\"%radius\"]";
						append = append.replaceAll("%caption", x.getCaption());
						append = append.replaceAll("%treeSize", Integer.toString(x.getTreeSize()));
						append = append.replaceAll("%posx", Double.toString(x.getxPos()));
						append = append.replaceAll("%posy", Double.toString(x.getyPos()));
						append = append.replaceAll("%radius", Double.toString(x.getRadius()));
						writer.append(append);
					}
					writer.append("\n");
					togo2.addAll(x.getChildren());
				}
				togo.clear();
				togo.addAll(togo2);
				togo2.clear();
			}
			writer.append("}");
			writer.close();
		} catch (IOException e) {
			System.out.println("Problem occured:");
			e.printStackTrace();
		}
		System.out.println("Export completed.");
	}

	/**
	 * Exports the graph! Every data, which is determined by now will be written
	 * into the file
	 * 
	 * @param root
	 *            The rootNode where to start. Every other node with posx=0.0,
	 *            posy=0.0 will be seen as without position data!
	 * @param ofile
	 *            Filename where to export. File must exist!
	 */
	public static void exportFile(GraphNode root, String replaceAll, boolean b) {
		exportFile(root, replaceAll, b, 1);
	}
}
