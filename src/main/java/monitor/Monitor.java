package monitor;

import java.util.ArrayList;

public class Monitor {

	private Poster p;
	private ArrayList<String> urgentMessages = new ArrayList<String>();
	private ArrayList<String> errorMessages = new ArrayList<String>();

	public enum MVariables {
		URGENT_MSG, ERROR_MSG, LINES_IMPORTED, LINES_TOTAL
	}

	public enum MStates {
		TERMINAL, LOADING_BAR, WINDOW
	}
	
	private MStates state = MStates.TERMINAL;

	private int linesImported;
	private int linesTotal;

	public Monitor() {
		p = new Poster(this);
		p.start();
	}

	public void postUrgentMessage(String msg) {
		this.urgentMessages.add(msg);
	}

	public void postErrorMessage(String msg) {
		this.errorMessages.add(msg);
	}

	public void setVariable(Monitor.MVariables v, String msg) {

	}

	public void setVariable(Monitor.MVariables v, int msg) {
		switch (v) {
		case LINES_IMPORTED:
			this.linesImported = msg;
			break;
		case LINES_TOTAL:
			this.linesTotal = msg;
			break;
		default:
			break;
		}
	}

	ArrayList<String> getUrgentMessages() {
		ArrayList<String> res = urgentMessages;
		urgentMessages = new ArrayList<String>();
		return res;
	}

	ArrayList<String> getErrorMessages() {
		ArrayList<String> res = errorMessages;
		errorMessages = new ArrayList<String>();
		return res;
	}

	MStates getState() {
		return state;
	}

	public void setState(MStates state) {
		this.state = state;
	}

	public int getLinesImported() {
		return linesImported;
	}

	public int getLinesTotal() {
		return linesTotal;
	}
	
	

}
