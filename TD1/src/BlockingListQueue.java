import java.util.LinkedList;

/**
 * Basic implementation with a LinkedList.
 * synchronization on this inspired by Nico Bernt
 */
public class BlockingListQueue implements URLQueue {

	private final LinkedList<String> queue;

	public BlockingListQueue() {
		this.queue = new LinkedList<String>();
	}

	public boolean isEmpty() {
		synchronized(this) {
			return this.queue.size() == 0;
		}
	}

	public boolean isFull() {
		synchronized(this) {
			return false;
		}
	}

	public void enqueue(String url) {
		synchronized(this) {		
			this.queue.add(url);
			System.out.println("I am notifying");
			notify();
		}
	}

	public String dequeue() {
		synchronized(this) {
			String url = "";
			while(isEmpty()) {
				try {
					wait();
					System.out.println("I am wating");
				} catch (InterruptedException e) {
					url = "**STOP**";
					Thread.currentThread().interrupt();
				}
			}
			url = this.queue.remove();
			if (url.equals("**STOP**")) {
				System.out.println("about to interrupt");
				Thread.currentThread().interrupt();
			}
			return url;
		}


	}

}
