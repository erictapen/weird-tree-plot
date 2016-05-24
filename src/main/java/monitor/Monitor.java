package monitor;

import java.util.ArrayList;

public class Monitor {

	private Poster p;
	private ArrayList<String> urgentMessages = new ArrayList<String>();
	private ArrayList<String> errorMessages = new ArrayList<String>();

	public enum MVariable {
		URGENT_MSG, ERROR_MSG, LINES_IMPORTED, LINES_TOTAL
	}

	public enum MState {
		TERMINAL, LOADING_BAR, WINDOW
	}

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

	public void setState(Monitor.MVariable v, String msg) {

	}

	public void setState(Monitor.MVariable v, int msg) {

	}

	ArrayList<String> getUrgentMessages() {
		return errorMessages;
	}

	ArrayList<String> getErrorMessages() {
		return urgentMessages;
	}

}
