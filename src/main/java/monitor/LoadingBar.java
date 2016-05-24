package monitor;

public class LoadingBar extends TerminalElement {

	private int from;
	private int to;
	private String caption;

	public LoadingBar(int from, int to, String caption) {
		super();
		this.from = from;
		this.to = to;
		this.caption = caption;
	}

	public String print(int w, int h) {
		String res = caption + "\n[";
		float frac = from/to;
		for (int i = 0; i < frac*(w-2); i++)
			res += "#";
		for (int i = 0; i < (1-frac)*(w-2); i++)
			res += " ";
		res += "]";
		return res;
	}

}
