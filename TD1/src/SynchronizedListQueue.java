import java.util.LinkedList;

/**
 * Basic implementation with a LinkedList.
 */
public class SynchronizedListQueue implements URLQueue {

	private final LinkedList<String> queue;
	private Object lock = new Object();

	public SynchronizedListQueue() {
		this.queue = new LinkedList<String>();
	}

	public boolean isEmpty() {
		synchronized(lock) {
			return this.queue.size() == 0;
		}
	}

	public boolean isFull() {
		return false;
	}

	public void enqueue(String url) {
		synchronized(lock) {		
			this.queue.add(url);
		}
	}

	public String dequeue() {
		synchronized(lock) {
			return this.queue.remove();
		}
	}

}