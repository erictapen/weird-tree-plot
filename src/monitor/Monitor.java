package monitor;

public class Monitor extends Thread {

	private int terminalWidth;
	private int terminalHeight;

	private ImportM importM = new ImportM();
	private PlotM plotM = new PlotM();
	private ExportM exportM = new ExportM();

	public enum State {
		IMPORT, PLOT, EXPORT
	}

	State state;

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
		if (importM.error == ImportM.Error.NONE)
			return importM.getStatusString();
		else
			return importM.getErrorMessage();
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
