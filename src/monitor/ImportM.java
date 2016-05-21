package monitor;

public class ImportM {
	String importfile;
	String rootcaption;
	int nodesImported;
	int uniqueNodesImported;
	
	Monitor parent = null;

	public enum Error {
		NONE, FILE_NOT_FOUND, IO_EXCEPTION
	}

	Error error = Error.NONE;

	public String getErrorMessage() {
		switch (error) {
		case NONE:
			return "ha";
		case FILE_NOT_FOUND:
			return "ha";
		case IO_EXCEPTION:
			return "ha";
		}
		return "ha";
	}

	public enum State {
		STARTING, IMPORTING, LINES_LOADED, UPDATING_LEEF_SIZES, NOT_UPDATING_LEEF_SIZES, GRAPH_IS_PLOTTED, GRAPH_IS_NOT_PLOTTED, COMPLETED
	}

	State state = State.STARTING;

	public String getStatusString() {
		switch (state) {
		case STARTING:
			return "Starting import of file " + importfile + "\n";
		case IMPORTING:
			return "\r" + nodesImported + " Nodes imported.";
		case LINES_LOADED:
			return "\n";
		case UPDATING_LEEF_SIZES:
			return "It seems like, the imported file doesn't have any information about "
					+ "subTreeSize. This must be updated now.\n";
		case NOT_UPDATING_LEEF_SIZES:
			return "Found information about leef sizes in file. If you want to recompute this information, erase it on at least node.\n";
		case GRAPH_IS_NOT_PLOTTED:
			return "Graph is not plotted yet.\n";
		case GRAPH_IS_PLOTTED:
			return "It appears, that the graph is already plotted. If you want to "
					+ "force plot it, change the radius of root to something different than 1.0.\n";
		case COMPLETED:
			return "Import completed successfull.\n";
		}
		return "";
	}
	
	public ImportM(Monitor parent) {
		super();
		this.parent = parent;
	}

	public void setImportfile(String importfile) {
		this.importfile = importfile;
	}

	public void setRootcaption(String rootcaption) {
		this.rootcaption = rootcaption;
	}

	public void setNodesImported(int nodesImported) {
		this.nodesImported = nodesImported;
	}

	public void setUniqueNodesImported(int uniqueNodesImported) {
		this.uniqueNodesImported = uniqueNodesImported;
	}

	public void setError(Error error) {
		this.error = error;
	}

	public void setState(State state) {
		this.state = state;
	}

}
