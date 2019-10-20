
public class ConnectedLayer implements Layer {
	private static Layer destLayer = null;
	private String connectedHost;
	private int connectedPort;
	private int connectedId;
	private int packetNumber = 0;
	private final String ack = "--ACK--";

	public ConnectedLayer(String host, int port, int id) {
		connectedHost = host;
		connectedPort = port;
		connectedId = id;
		String payload = connectedId + ";" + packetNumber + ";" + "--HELLO--";
		GroundLayer.deliverTo(this);
		GroundLayer.send(payload, host, port);
		String currLayer = this.getClass().getName();
		if (destLayer != null) {
			destLayer.receive(payload, currLayer.subSequence(0, currLayer.length()-2).toString());
		}
	}

	@Override
	public void send(String payload) {
		packetNumber++;
		String newPayload = "";
		String load[] = payload.split(";");
		if (load.length > 1) {
			newPayload = payload;
		} else {
			newPayload = connectedId + ";" + packetNumber + ";" + payload;
		}
		GroundLayer.send(newPayload, connectedHost, connectedPort);
	}

	@Override
	public void receive(String payload, String source) {
		System.out.println('"' + payload + "\" from " + source);
		if (payload != null && !payload.isEmpty()) {
			String[] load = payload.split(";");
			String connectionId = load[0];
			String packetNumber = load[1];
			String message = load[2].trim();
			if (!message.equals(ack)) {
				String newPayload = connectionId + ";" + packetNumber + ";" + ack;
				send(newPayload);
			} else {
				return;
			}
		}
	}

	@Override
	public void deliverTo(Layer above) {
		if (above != null) {
			destLayer = above;
		} else {
			destLayer = null;
		}
	}

	@Override
	public void close() {
		GroundLayer.close();
	}

}
