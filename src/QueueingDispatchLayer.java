import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class QueueingDispatchLayer implements Layer {
	 //inspired by https://stackoverflow.com/questions/11149707/best-implementation-of-java-queue
	private static Queue<ConnectionParameters> queue = new LinkedList<ConnectionParameters>();
	private static Map<Integer, Layer> table = new HashMap<Integer, Layer>();
	private static ArrayList<Integer> seenIds = new ArrayList<Integer>();
	private static Layer dispatcher = null;
	
	private QueueingDispatchLayer() { // singleton pattern
	}

	
	public static synchronized void start() {
		if (dispatcher == null) {
			dispatcher = new QueueingDispatchLayer();
		}
		GroundLayer.deliverTo(dispatcher);
	}
	
	public static synchronized void register(Layer layer, int sessionId) {
		if (dispatcher != null) {
			table.put(sessionId, layer);
			GroundLayer.deliverTo(dispatcher);
		} else {
			GroundLayer.deliverTo(layer);
		}
	}

	@Override
	public void send(String payload) {
		throw new UnsupportedOperationException("don't use this for sending");
	}

	@Override
	public void receive(String payload, String source) {
		String[] load = payload.split(";");
		int connectionId = Integer.parseInt(load[0].trim());
		String[] address = source.split(":");
		String host = address[0].trim();
		String[] hostnames = host.split("/");
		String hostname = hostnames[1];
		int port = Integer.parseInt(address[1].trim());
		ConnectionParameters params = new ConnectionParameters(connectionId, hostname, port);
		System.out.println(params.toString());
		if (!seenIds.contains(connectionId)) {
			if(queue.size() < 5) {
				//System.out.println("adding to queue");
				synchronized(this) {
					seenIds.add(connectionId);
					queue.add(params);
					notifyAll();
				}
			}
		} else {
			System.out.println("was already in queue");
				Layer connectedLayer = table.get(connectionId);
				connectedLayer.receive(payload, source);
		}
	}

	@Override
	public void deliverTo(Layer above) {
		throw new UnsupportedOperationException("don't support a single Layer above");
	}

	@Override
	public void close() {
		// nothing
	}
	
	public static ConnectionParameters accept() {
		ConnectionParameters params = null;
		if(!queue.isEmpty()) {
			params = queue.poll();
		}
		return params;
	}

}
