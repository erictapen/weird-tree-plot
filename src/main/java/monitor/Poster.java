package monitor;

import java.util.ArrayList;

public class Poster extends Thread {

	private Monitor m;

	public Poster(Monitor monitor) {
		m = monitor;
	}

	@Override
	public void run() {
		while (!this.isInterrupted()) {
			ArrayList<String> urg = m.getUrgentMessages();
			ArrayList<String> err = m.getErrorMessages();
			if (!urg.isEmpty())
				for (String x : urg)
					System.out.println(x);
			if (!err.isEmpty())
				for (String x : err)
					System.out.println(x);

			try {
				Poster.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
