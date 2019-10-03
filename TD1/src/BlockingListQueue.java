import java.util.LinkedList;

/**
 * Basic implementation with a LinkedList.
 */
public class BlockingListQueue implements URLQueue {

	private final LinkedList<String> queue;
	private Object lock = new Object();
	private final int size;

	public BlockingListQueue(int size) {
		this.queue = new LinkedList<String>();
		this.size = size;
	}

	public boolean isEmpty() {
		synchronized(lock) {
			return this.queue.size() == 0;
		}
	}

	public boolean isFull() {
		return this.queue.size() == size;
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
