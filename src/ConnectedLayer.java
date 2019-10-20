import java.util.Timer;
import java.util.TimerTask;

public class ConnectedLayer implements Layer {
	private static Layer destLayer = null;
	private String connectedHost;
	private int connectedPort;
	private int connectedId;
	private int packetNumber = 0;
	private final String ack = "--ACK--";
	private static final Timer TIMER = new Timer("TickTimer", true);
	private boolean received = false;

	public ConnectedLayer(String host, int port, int id) {
		connectedHost = host;
		connectedPort = port;
		connectedId = id;
		String payload = connectedId + ";" + packetNumber + ";" + "--HELLO--";
		GroundLayer.deliverTo(this);
		String currLayer = this.getClass().getName();
		GroundLayer.send(payload, host, port);
		if (destLayer != null) {
			destLayer.receive(payload, currLayer.subSequence(0, currLayer.length()-2).toString());
		}
	}

	@Override
	public void send(String payload) {
		packetNumber++;
		String load[] = payload.split(";");
		TimerTask task = new TimerTask() {
			public void run() {
				String newPayload = "";
				if (load.length > 1) {
					newPayload = payload;
				} else {
					newPayload = connectedId + ";" + packetNumber + ";" + payload;
				}
				received = false;
				GroundLayer.send(newPayload, connectedHost, connectedPort);
				System.out.println("sending...");
				synchronized(this) {
					while (!received) {
						try {
							System.out.println("waiting");
							this.wait();
						} catch (InterruptedException e) {
							System.err.println(e.getMessage());
						}
					}
					this.cancel();
				}
			}
		};
		TIMER.schedule(task, 0, 1000);
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
				synchronized(this) {
					received = true;
					notify();
					System.out.println("notifying...");
				}
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
