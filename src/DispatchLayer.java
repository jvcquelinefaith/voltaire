import java.util.HashMap;
import java.util.Map;

public class DispatchLayer implements Layer {

	private static Map<Integer, Layer> table = new HashMap<Integer, Layer>();
	private static Layer dispatcher = null;

	public static synchronized void start() {
		if (dispatcher == null)
			dispatcher = new DispatchLayer();
		GroundLayer.deliverTo(dispatcher);
	}

	@SuppressWarnings("boxing")
	public static synchronized void register(Layer layer, int sessionId) {
		System.out.println("registering Layer");
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

	@Override
	public void receive(String payload, String source) {
		System.out.println("payload: " + payload + "source: " + source);
		String[] load = payload.split(";");
		int connectionId = Integer.parseInt(load[0].trim());
		Layer connectedLayer;
		
		if (table.containsKey(connectionId)) {
			System.out.println("contained id");
			connectedLayer = table.get(connectionId);
		} else {
			System.out.println("did not contain id");
			String[] address = source.split(":");
			String hostname = address[0].trim();
			String[] hostlist = hostname.split("/");
			String host = "http://" + hostlist[0].trim();
			int port = Integer.parseInt(address[1].trim());
			FileReceiver receiver = new FileReceiver(host, port, connectionId);
			Layer subLayer = receiver.getSubLayer();
			connectedLayer = subLayer;
		}
		System.out.println("recieving from DL, sending to CL");
		connectedLayer.receive(payload, source);
	}

	@Override
	public void deliverTo(Layer above) {
		throw new UnsupportedOperationException("don't support a single Layer above");
	}

	@Override
	public void close() { // nothing
	}

}