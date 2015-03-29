
public class Monitor implements Runnable {
	private Thread thread;
	
	//Variables for the monitor.
	private Node node;
	private int[] benchmarks;
	public volatile static NetworkController controller;
	
	public Monitor(Node dslNode, int[] benchmarks){
		node = dslNode;
		this.benchmarks = benchmarks;
	}
	
	public void run() {
		//Executes when the program runs.
		while(true){
			//Performs monitor and then detection mode.
			boolean success = monitorMode();
			if (!success) detectionMode();
			else System.out.println("Metrics have passed.");
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				
				//Aborts the thread.
				System.out.println("\nAborting " + node.getType().getName() + ".");
				break;
			}
		}
	}

	public void start() {
		//Checks if the thread is already running.
		if (thread == null){
			thread = new Thread(this);
			thread.start();
		}
	}
	
	private boolean monitorMode(){
		//We request data from the controller.
		int[] currentQoS = null;
				
		try {
			synchronized(controller) {
				currentQoS = controller.requestData(node.getType());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			return true;
		}			
		
		//We need to check whether the metrics meet standards.
		return checkMetrics(currentQoS);
	}
	
	private boolean checkMetrics(int[] currentQoS) {	
		//Loops through all the QoS values to check.
		for (int i = 0; i < currentQoS.length - 1; i++){
			if (i < 3){
				if (currentQoS[i] >= benchmarks[i]){
					return false;
				}
			} else {
				if ((currentQoS[i] < benchmarks[i] &&
				    currentQoS[i + 1] <= benchmarks[i + 1]) ||
				    currentQoS[i + 1] < benchmarks[i + 1]){
					return false;
				}
			}
		}
		return true;
	}

	private void detectionMode(){
		System.out.println("Detecting Error.");
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		controller.notifyDetected(null, null);
	}
}
