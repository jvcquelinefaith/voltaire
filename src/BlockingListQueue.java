import java.util.LinkedList;

/**
 * Basic implementation with a LinkedList.
 */
public class BlockingListQueue implements URLQueue {

	private final LinkedList<String> queue;

	public BlockingListQueue() {
		this.queue = new LinkedList<String>();
	}

	public synchronized boolean isEmpty() {
		return this.queue.size() == 0;
	}

	public synchronized boolean isFull() {
		return false;
	}

	public synchronized void enqueue(String url) {	
		this.queue.add(url);
		notifyAll();
	}

	public synchronized String dequeue() {
		if(this.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return this.queue.remove();
	}

}
