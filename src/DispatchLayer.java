import java.util.HashMap;
import java.util.Map;

public class DispatchLayer implements Layer {

	private static Map<Integer, Layer> table = new HashMap<Integer, Layer>();
	private static Layer dispatcher = null;

	public static synchronized void start() {
		if (dispatcher == null) {
			dispatcher = new DispatchLayer();
		}
		GroundLayer.deliverTo(dispatcher);
	}

	@SuppressWarnings("boxing")
	public static synchronized void register(Layer layer, int sessionId) {
		if (dispatcher != null) {
			table.put(sessionId, layer);
			GroundLayer.deliverTo(dispatcher);
		} else {
			GroundLayer.deliverTo(layer);
		}
	}

	private DispatchLayer() { // singleton pattern
	}

	@Override
	public void send(String payload) {
		throw new UnsupportedOperationException("don't use this for sending");
	}
	
	public int getNewId() {
		int newId = (int) (Math.random() * Integer.MAX_VALUE);
		while(table.containsKey(newId)) {
			newId = (int) (Math.random() * Integer.MAX_VALUE);
		}
		return newId;
	}

	@Override
	public void receive(String payload, String source) {
		String[] load = payload.split(";");
		
		int connectionId = Integer.parseInt(load[0].trim());
		if (table.containsKey(connectionId)) {
			Layer connectedLayer = table.get(connectionId);
			connectedLayer.receive(payload, source);
		} else {
			String[] address = source.split(":");
			String host = address[0].trim();
			String[] hostnames = host.split("/");
			String hostname = hostnames[1];
			int port = Integer.parseInt(address[1].trim());
			Thread thread = new Thread() {
				public void run() {
					FileReceiver receiver = new FileReceiver(hostname, port, getNewId());
					Layer subLayer = receiver.getSubLayer();
					subLayer.receive(payload, source);
					register(subLayer, connectionId);
				}
			};
			thread.start();
		}		
	}

	@Override
	public void deliverTo(Layer above) {
		throw new UnsupportedOperationException("don't support a single Layer above");
	}

	@Override
	public void close() { // nothing
	}

}