package plot;

/** This class is for parallelized collision detection. It will run as a seperate thread. Each thread grabs
 * itself a portion of the movingNodes and checks it against all existing existing nodes. After every thread
 * is ready, nodes are updated and the next iteration begins.
 * @author justin
 *
 */
public class CollisionDetector extends Thread{
	
	
	
	CollisionDetector() {
		super();
	}
	
	public void run() {
		
	}
	
	
	
}
