import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

class QueryUrl implements Runnable {
	String requestedURL;
	String proxyHost;
	int proxyPort; 

	public QueryUrl(String requestedURL, String proxyHost, int proxyPort) {
		this.requestedURL = requestedURL;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}
	public void run() {		
		Xurl.query(requestedURL, proxyHost, proxyPort);	
	}
}

class QueryPoolUrl implements Runnable {
	URLQueue queue;
	String proxyHost;
	int proxyPort; 
	
	public QueryPoolUrl(URLQueue queue, String proxyHost, int proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.queue = queue;
	}
	public void run() {	
		while (!Thread.interrupted()) {	
			String url = queue.dequeue();
			if (url != null) {
				System.out.println("dequeing " + url + " by " + Thread.currentThread().getName());		
				Xurl.query(url, proxyHost, proxyPort);
			}
		}	
	}
}

public class Wget {

	public static void doIterative(String requestedURL, String proxyHost, int proxyPort) {
		final URLQueue queue = new ListQueue();
		final HashSet<String> seen = new HashSet<String>();
		URLprocessing.handler = new URLprocessing.URLhandler() {
			// this method is called for each ma  tched url
			public void takeUrl(String url) {
				// to be completed
				if (!seen.contains(url)) {
					queue.enqueue(url);
				}
			}
		};
		// to start, we push the initial url into the queue
		URLprocessing.handler.takeUrl(requestedURL);
		while (!queue.isEmpty()) {
			String url = queue.dequeue();
			if(!seen.contains(url)) {
				seen.add(url);
				System.out.println(url);
				Xurl.query(url, proxyHost, proxyPort); // don't change this call
			}
		}
	}

	public static void doMultiThreaded(String requestedURL, String proxyHost, int proxyPort) {
		final int initial_thread_count = Thread.activeCount();
		// to be completed at exercise 5
		final URLQueue queue = new SynchronizedListQueue();
		final HashSet<String> seen = new HashSet<String>();

		URLprocessing.handler = new URLprocessing.URLhandler() {
			// this method is called for each matched url
			public void takeUrl(String url) {
				// to be completed
				synchronized(seen) {
					if (!seen.contains(url)) {
						seen.add(url);
						queue.enqueue(url);
					}
				}
			}
		};
		// to start, we push the initial url into the queue
		URLprocessing.handler.takeUrl(requestedURL);
		int final_thread_count = 0;
		while (!queue.isEmpty() || (initial_thread_count < final_thread_count)) {
			if (!queue.isEmpty()) {
				String url = queue.dequeue();
				Thread thread = new Thread(new QueryUrl(url, proxyHost, proxyPort));
				thread.start();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final_thread_count = Thread.activeCount();
		}

	}



	public static void doThreadedPool(int poolSize, String requestedURL, String proxyHost, int proxyPort) {
		// to be completed at exercise 6
		final URLQueue queue = new BlockingListQueue();
		final HashSet<String> seen = new HashSet<String>();
		final ArrayList<Thread> pool = new ArrayList<Thread>();
		final int initial_thread_count = Thread.activeCount();

		URLprocessing.handler = new URLprocessing.URLhandler() {
			// this method is called for each matched url
			public void takeUrl(String url) {
				// to be completed
				synchronized(seen) {
					if (!seen.contains(url)) {
						seen.add(url);
						queue.enqueue(url);
						System.out.println("adding " + url + " by " + Thread.currentThread().getName());
					}
				}
			}
		};
		// to start, we push the initial url into the queue
		URLprocessing.handler.takeUrl(requestedURL);
		
		int final_thread_count = 0;
		
		while (!queue.isEmpty() || initial_thread_count < final_thread_count) {
			while(pool.size() < poolSize) {
				System.out.println("creating threads");
				Thread thread = new Thread(new Runnable() {
					public void run() {	
						while (!Thread.currentThread().isInterrupted()) {	
							String url = queue.dequeue();
							if (url != null) {
								if (url.equals("**STOP**")) {
									break;
								}
								System.out.println("dequeing " + url + " by " + Thread.currentThread().getName());		
								Xurl.query(url, proxyHost, proxyPort);
							} 
						}	
					}
				});
				thread.start();
				pool.add(thread);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				System.out.println("interrupted");
			}
			int count = 0;
			if(queue.isEmpty()) {
				for (Thread t : pool) {
					if(!t.getState().equals(Thread.State.RUNNABLE)) {
						count ++;
					} 
				}
				System.out.println("count: " + count);
				if (count == poolSize) {
					for (Thread t : pool) {
						System.out.println(t.getState());
						queue.enqueue("**STOP**");
						t.interrupt();
						System.out.println("Thread count: " + Thread.activeCount());
					}
				}
			}
			final_thread_count = Thread.activeCount();
			System.out.println("Thread count: " + Thread.activeCount());
			if(Thread.activeCount() == initial_thread_count) {
				return;
			}
		}
	}


	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java Wget url [proxyHost proxyPort]");
			System.exit(-1);
		}
		String proxyHost = null;
		if (args.length > 1)
			proxyHost = args[1];
		int proxyPort = -1;
		if (args.length > 2)
			proxyPort = Integer.parseInt(args[2]);
		//doIterative(args[0], proxyHost, proxyPort);
		//doMultiThreaded(args[0], proxyHost, proxyPort);
		doThreadedPool(3, args[0], proxyHost, proxyPort);
	}

}