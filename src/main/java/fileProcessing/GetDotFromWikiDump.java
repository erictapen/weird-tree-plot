package fileProcessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class GetDotFromWikiDump {
	
	private static String ifile = "/home/justin/Downloads/dewiki-latest-pages-articles.xml";
	private static String ofile = "/home/justin/Dropbox/java/Wikipedia Crawl/test.dot";
	private static int iterations = 100;
	
	public static void main(String[] args) {
		String page = "";
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(ofile, "UTF-8");
		} catch (FileNotFoundException e1) {
			System.out.println("File \"" + ofile + "\" not found. Abort.");
			e1.printStackTrace();
			return;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return;
		}
		
		writer.println("digraph wikiMap {");
		
		try (BufferedReader br = new BufferedReader(new FileReader(ifile))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("<page>")) {
					page = "";
				} else if(line.contains("</page>")){
					extractRelationToFile(page, writer);
				} else {
					page += line + "\n";
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File \"" + ifile + "\" not found. Abort.");
			e.printStackTrace();
			return;
		} catch (IOException e) {
			System.out.println("IOException. Abort.");
			e.printStackTrace();
			return;
		}
		writer.close();
	}

	private static void extractRelationToFile(String page, PrintWriter writer) {
		if(iterations >= 0) {
			if(iterations % 1 == 0) System.out.println(iterations);
			writer.println(extractScope(page, "<title>", "</title>") + " --> " 
					+ extractFirstLink(extractScope(page, "<text xml:space=\"preserve\">", "== ")));
			iterations--;
		} else {
			writer.println("}");
			writer.close();
			System.exit(0);
		}
	}
	
	private static String extractScope(String str, String lbrace, String rbrace) {
		int open = str.indexOf(lbrace);
		int close = str.indexOf(rbrace);
		if(open!=-1 && close!=-1 && open < close) {
			return str.substring(open + lbrace.length(), close);
		}
		return "";
	}
	
	private static String extractFirstLink(String str) {
		String res = "";
		/*
		//boolean outside = true;
		int normalbrace = 0;  	// ( | )
		//int curlybrace = 0;  	// { | }
		//int hardbrace = 0;  	// [ | ]
		int doublecurly = 0;  	// {{ | }}
		int filetag = 0;		// [[Datei: | ]]
		
		
		while(str.length() > 1) {
			//System.out.println(str + normalbrace + doublecurly);
			if(str.startsWith("{{")) {
				doublecurly++;
			} else if(doublecurly!=0 && str.startsWith("}}")) {
				doublecurly--;
			}
			if(doublecurly==0 && str.startsWith("[[Datei:")) {
				filetag++;
			} else if(filetag!=0 && str.startsWith("]]")) {
				filetag--;
			}
			if(doublecurly==0 && filetag==0 && str.startsWith("(")) {
				normalbrace++;
			} else if(normalbrace!=0 && doublecurly==0 && str.startsWith("(")) {
				normalbrace--;
			}
			
			
			if(str.startsWith("[[") && normalbrace==0 && doublecurly==0 && filetag==0) {
				
				res = str.substring(2, str.indexOf("]]"));
				return res;
			}
			
			str = str.substring(1);
			
		}*/
		str = str.replaceFirst("(*)", "");
		return res;
	}

}
