package plot;

/** This class is for parallelized collision detection. It will run as a seperate thread. Each thread grabs
 * itself a copy of all movingNodes and checks it against its subset of all existing nodes. After every 
 * thread is finished, nodes are updated and the next iteration begins. Therefore, only the collision
 * detection is parallelized.
 * @author justin
 *
 */
public class CollisionDetector extends Thread{
	
	
	
	CollisionDetector() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 * Here does happen all the stuff in order to determine, which nodes do collide with others.
	 */
	public void run() {
		
	}
	
	
	
}
