
public class Monitor implements Runnable {
	private Thread thread;
	
	//Variables for the monitor.
	private Node node;
	private int[] benchmarks;
	private NetworkController controller;
	
	public Monitor(Node dslNode, int[] benchmarks, NetworkController data){
		node = dslNode;
		this.benchmarks = benchmarks;
		controller = data;
	}
	
	public void run() {
		//Executes when the program runs.
		while(true){
			//We request data from the controller.
			System.out.println(node.getType().getName() + " requesting QoS values.");
			int[] currentQoS = controller.requestData(node.getType());
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
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
}
