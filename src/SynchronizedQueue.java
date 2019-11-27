import java.util.LinkedList;

/**
 * Basic implementation with a LinkedList.
 */
public class SynchronizedQueue {

	private final LinkedList<String> queue;
	private Object lock = new Object();
	private int maxSize;

	public SynchronizedQueue(int size) {
		this.queue = new LinkedList<String>();
		this.maxSize = size;
	}

	public boolean isEmpty() {
		synchronized(lock) {
			return this.queue.size() == 0;
		}
	}

	public boolean isFull() {
		return false;
	}

	public void insert(String url) {
		synchronized(lock) {		
			if (this.queue.size() < maxSize) {
				this.queue.add(url);
			}
		}
	}

	public String get() {
		synchronized(lock) {
			return this.queue.remove();
		}
	}

}