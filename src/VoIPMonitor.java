
public class VoIPMonitor implements Runnable {
	private Thread thread;
	
	public VoIPMonitor(){
		
	}

	public void run() {
		
	}
	
	public void start() {
		//Checks if the thread is already running.
		if (thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
}
