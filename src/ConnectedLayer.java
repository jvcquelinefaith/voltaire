import java.util.Timer;
import java.util.TimerTask;

public class ConnectedLayer implements Layer {
	private static Layer destLayer = null;
	private String connectedHost;
	private int connectedPort;
	private int connectedId;
	private int packetNumber = 0;
	private int nextPacket = packetNumber+1;
	private String remoteConnectionId;
	private final String ack = "--ACK--";
	private final String hello = "--HELLO--";
	String currLayer = this.getClass().getName();
	private static final Timer TIMER = new Timer("TickTimer", true);

	public ConnectedLayer(String host, int port, int id) {
		connectedHost = host;
		connectedPort = port;
		connectedId = id;
		String payload = "--HELLO--";
		GroundLayer.deliverTo(this);
		send(payload);
	}

	@Override
	public void send(String payload) {
		TimerTask task = new TimerTask() {
			public void run() {
				String newPayload = "";
				String load[] = payload.split(";");
				if (load.length > 1) {
					newPayload = payload;
				} else {
					newPayload = connectedId + ";" + packetNumber + ";" + payload;
				}
				GroundLayer.send(newPayload, connectedHost, connectedPort);
			}
		};
		TIMER.schedule(task, 0, 300);
		synchronized (this) {
			try {
				//System.out.println("waiting");	
				this.wait();
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			}
		}
		task.cancel();
		packetNumber++;
		return;
	}

	@Override
	public void receive(String payload, String source) {
		//System.out.println('"' + payload + "\" from " + source);
		if (payload != null && !payload.isEmpty()) {
			String[] load = payload.split(";");
			String connectionId = load[0].trim();
			String packetNum = load[1].trim();
			String message = load[2].trim();
			if (message.equals(ack)) {
				//System.out.println("ack: their connectionId: " + connectedId + " their connection: " + connectionId + " my packet: " + packetNumber + " thier packet: " + packetNum);			
				if (connectionId.equals(Integer.toString(connectedId)) && packetNum.equals(Integer.toString(packetNumber))) {
					//System.out.println("connection id and packetNum same");	
					synchronized (this) {
						//System.out.println("notifying");	
						this.notifyAll();
					}
					return;
				}
			} else if (message.equals(hello)) {
				remoteConnectionId = connectionId;
				String newPayload = connectionId + ";" + packetNum + ";" + ack;
				GroundLayer.send(newPayload, connectedHost, connectedPort);
			} else if (connectionId.equals(remoteConnectionId)) {
				if (!message.equals(ack)) {
					String newPayload = connectionId + ";" + packetNum + ";" + ack;
					GroundLayer.send(newPayload, connectedHost, connectedPort);
					if (destLayer != null && packetNum.equals(Integer.toString(nextPacket))) {
						destLayer.receive(message, currLayer.subSequence(0, currLayer.length() - 2).toString());
						nextPacket++;
					}
				} else {
					synchronized (this) {
						this.notifyAll();
					}
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
