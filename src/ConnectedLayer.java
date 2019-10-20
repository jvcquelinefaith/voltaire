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
				GroundLayer.send(newPayload, connectedHost, connectedPort);
				System.out.println("sending...");
			}
		};
		TIMER.schedule(task, 0, 1000);
		synchronized(this) {
			while (!received) {
				try {
					System.out.println("waiting");
					wait();
					task.wait();
				} catch (InterruptedException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		System.out.println("about to cancel");
		task.cancel();
		TIMER.cancel();
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
					notifyAll();
					System.out.println("notifying...");
					return;
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
