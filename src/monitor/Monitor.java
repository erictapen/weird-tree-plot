package monitor;

public class Monitor extends Thread {

	private MsgPoster poster = new MsgPoster();

	private int terminalWidth;
	private int terminalHeight;

	private ImportM importM = new ImportM(this);
	private PlotM plotM = new PlotM();
	private ExportM exportM = new ExportM();

	public enum State {
		IMPORT, PLOT, EXPORT
	}

	State state = State.IMPORT;

	public Monitor() {
		super();

	}

	public void run() {
		poster.start();
		while (!Thread.currentThread().isInterrupted()) {
			this.terminalWidth = jline.TerminalFactory.get().getWidth();
			this.terminalHeight = jline.TerminalFactory.get().getHeight();
			this.postInfoString();
		}
	}

	private void postInfoString() {
		switch (state) {
		case IMPORT:
			postImportInfo();
		case PLOT:
			postPlotInfo();
		case EXPORT:
			postExportInfo();
		}
	}

	private void postImportInfo() {
		if (importM.error != ImportM.Error.NONE) {
			poster.addMsg(importM.getErrorMessage());
		}
		else if (importM.state == ImportM.State.IMPORTING) {
			poster.replaceLastMsg(importM.getStatusString());
		}
		else {
			poster.addMsg(importM.getStatusString());
		}

	}

	private void postPlotInfo() {
		// TODO Auto-generated method stub
	}

	private void postExportInfo() {
		// TODO Auto-generated method stub
	}

	public ImportM getImportM() {
		return importM;
	}

	public PlotM getPlotM() {
		return plotM;
	}

	public ExportM getExportM() {
		return exportM;
	}

}
