package monitor;

import java.util.ArrayList;

import monitor.Monitor.MStates;


public class Poster extends Thread {

	private int width = 0;
	private int height = 0;

	private Monitor m;

	public Poster(Monitor monitor) {
		m = monitor;
	}

	@Override
	public void run() {

		while (!this.isInterrupted()) {
			width = jline.TerminalFactory.get().getWidth();
			height = jline.TerminalFactory.get().getHeight();
			ArrayList<String> urg = m.getUrgentMessages();
			ArrayList<String> err = m.getErrorMessages();
			if (!urg.isEmpty())
				for (String x : urg)
					System.out.println(x);
			if (!err.isEmpty())
				for (String x : err)
					System.out.println(x);

			TerminalElement root = buildWindow();
			if(root!=null) {
				String out = root.print();
				for(int i=0; i<countLines(out); i++)
					out += "\r";
				System.out.println(out);
			}
			try {
				Poster.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private TerminalElement buildWindow() {
		switch (m.getState()) {
		case LOADING_BAR:
			return new LoadingBar(m.getLinesImported(), m.getLinesTotal(), " lines imported");
		default:
			break;
		}
		return null;
	}
	
	public static int countLines(String str) {
	    if(str == null || str.isEmpty())
	    {
	        return 0;
	    }
	    int lines = 1;
	    int pos = 0;
	    while ((pos = str.indexOf("\n", pos) + 1) != 0) {
	        lines++;
	    }
	    return lines;
	}
}
