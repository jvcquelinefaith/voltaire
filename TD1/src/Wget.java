import java.util.HashSet;

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

public class Wget {
	final static int initial_thread_count = Thread.activeCount();

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
		final URLQueue queue = new BlockingListQueue(poolSize);
		final HashSet<String> seen = new HashSet<String>();

		URLprocessing.handler = new URLprocessing.URLhandler() {
			// this method is called for each matched url
			public void takeUrl(String url) {
				// to be completed
				synchronized(seen) {
					if (!seen.contains(url) || !queue.isFull()) {
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