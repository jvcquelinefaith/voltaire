import java.util.HashSet;

public class Wget {

  public static void doIterative(String requestedURL, String proxyHost,
      int proxyPort) {
    final URLQueue queue = new ListQueue();
    final HashSet<String> seen = new HashSet<String>();
    URLprocessing.handler = new URLprocessing.URLhandler() {
      // this method is called for each matched url
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
    	 Xurl.query(url, proxyHost, proxyPort); // don't change this call
      }
    }
  }

  public static void doMultiThreaded(String requestedURL, String proxyHost, int proxyPort) {
    // to be completed at exercise 5
  }

  public static void doThreadedPool(int poolSize, String requestedURL, String proxyHost, int proxyPort) {
    // to be completed at exercise 6
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
    doIterative(args[0], proxyHost, proxyPort);
    // doMultiThreaded(args[0], proxyHost, proxyPort);
    // doThreadedPool(3, args[0], proxyHost, proxyPort);
  }

}