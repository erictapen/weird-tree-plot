package monitor;

import java.util.ArrayList;

public class MsgPoster extends Thread {

	private ArrayList<String> todo = new ArrayList<String>();

	private String justposted = "";

	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				Monitor.sleep(200);
				if (!todo.isEmpty()) {
					if (!todo.get(0).equals(justposted)) {
						System.out.print(todo.get(0));
						justposted = todo.get(0);
					}
					todo.remove(0);
				}
//				System.out.println(todo.size());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void addMsg(String msg) {
		if (!todo.isEmpty() && todo.get(todo.size() - 1).equals(msg))
			return;
		todo.add(msg);
	}

	public synchronized void replaceLastMsg(String msg) {
		if (todo.isEmpty())
			todo.add(msg);
		else
			todo.set(todo.size() - 1, msg);
	}
}
