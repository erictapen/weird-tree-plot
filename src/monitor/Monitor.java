package monitor;

public class Monitor extends Thread {

	private int terminalWidth;
	private int terminalHeight;

	private ImportM importM = new ImportM();
	private PlotM plotM = new PlotM();
	private ExportM exportM = new ExportM();

	public enum MState {
		IMPORT, PLOT, EXPORT
	}

	MState state;
	
	public Monitor() {
		super();
	}

	public void run() {
		this.terminalWidth = jline.TerminalFactory.get().getWidth();
		this.terminalHeight = jline.TerminalFactory.get().getHeight();
		System.out.println(this.generateInfoString());
		try {
			Monitor.sleep(500);
		} catch (InterruptedException e) {

		}
	}

	private String generateInfoString() {
		switch (state) {
		case IMPORT:
			return generateImportInfo();
		case PLOT:
			return generatePlotInfo();
		case EXPORT:
			return generateExportInfo();
		}
		return "";
	}

	private String generateImportInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	private String generatePlotInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	private String generateExportInfo() {
		// TODO Auto-generated method stub
		return null;
	}
}
