import org.apache.commons.lang3.ArrayUtils;

import processing.core.PApplet;


public class EntryPoint {
	public static void main(String[] args) {
		PApplet.main(ArrayUtils.addAll(new String[] { "draw.GraphDraw"}, args));
	}

}
