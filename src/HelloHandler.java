import java.util.concurrent.SynchronousQueue;

public class HelloHandler implements SimpleMessageHandler, Runnable {
	private SynchronousQueue<String> incoming = new SynchronousQueue<String>();
    private MuxDemux myMuxDemux = null;
    
    public void setMuxDemux(MuxDemux md) {
        myMuxDemux = md;
    }
	

	@Override
	public void run() {
		while (true){
			synchronized(incoming) {
	            String msg = incoming.poll();
	            if (msg != null) {
	            	myMuxDemux.send(msg); 
	            }
			} 
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }	
	}

	@Override
	public void handleMessage(String m) {
        synchronized(incoming) {
			incoming.offer(m);
		}		
	}

}
