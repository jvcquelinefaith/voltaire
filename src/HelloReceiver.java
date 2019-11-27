import java.time.LocalTime;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloReceiver implements SimpleMessageHandler, Runnable {
	private String hello = "HELLO";
	private String colon = ";";
	private String senderID = "([a-zA-Z0-9]{1,16})";
	private String sequence = "([-]{0,1}[0-9]+)";
	private String hello_interval = "([0-9]+)";
	private String num_peers = "([0-9]+)";
	private String peers = "([A-Za-z0-9]{1,16})*";
	private String regex = hello + colon + senderID + colon + sequence + colon + hello_interval + colon + num_peers
			+ "(" + colon + "*)" + peers;

	private Pattern pattern;
	private Matcher matcher;

	private MuxDemux muxDemux;

	private SynchronousQueue<String> incoming = new SynchronousQueue<String>();
	
	private String myName = "";

	@Override
	public void setMuxDemux(MuxDemux md) {
		muxDemux = md;
	}

	@Override
	public void handleMessage(String m) {
		incoming.offer(m);
	}

	@Override
	public void run() {
		pattern = Pattern.compile(regex);

		System.out.println(pattern);
		while (true) {
			
			String m = null;
			try {
				m = incoming.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (m != null) {

				matcher = pattern.matcher(m);
				
				if (muxDemux.getMyName() != null && !muxDemux.getMyName().isEmpty()) {
					myName = muxDemux.getMyName();
				}

				if (matcher.find()) {
					System.out.println("RECEIVE received: " + m);

					String[] msg = m.split(";");
					String peerId = msg[1];
					String peerSeq = msg[2];
					String helloInt = msg[3];
					ArrayList<String> peers = new ArrayList<String>();
					int numPeers = Integer.parseInt(msg[4]);
					
					//Enforcing 255 peer max
					if (numPeers > 0) {
						if ( numPeers > 255) {
							numPeers = 255;
						}
						for (int i = 0; i < numPeers - 1; i++) {
							peers.add(msg[i]);
						}
					}
					synchronized (muxDemux.peersList) {
						LocalTime time = LocalTime.now();
						if (muxDemux.peersList.containsKey(peerId)) {
							Peer current = muxDemux.peersList.get(peerId);
							if (time.isAfter(current.getExpirationTime())) {
								muxDemux.peersList.remove(peerId);
								System.out.println("removed " + peerId);
							} else { 
								//System.out.println(peerId + " will expire at " + current.getExpirationTime().toString());
							}
							if (!peers.isEmpty() && peers.contains(peerId)) {
								if (!current.getPeerSeq().equals(peerSeq)) {
									current.setState("INCONSISTENT");
								} else if (current.getState().equals("INCONSISTENT")) {
									current.setState("INCONSISTENT");
								} else if (current.getState().equals("SYNCHRONISED")
										&& current.getPeerSeq().equals(peerSeq)) {
									current.setState("SYNCHRONISED");
								}
							}
						} else {
//							System.out.println("adding to peersList");
							if (!peerId.equals(myName)) {
								//Enforcing 255 helloInt max
								if (Integer.parseInt(helloInt) > 255) {
									helloInt = "255";
								}
								Peer current = new Peer(peerId, "-1", LocalTime.now().plusSeconds(Long.parseLong(hello)),
										"HEARD");
								muxDemux.peersList.put(peerId, current);
							}
						}
					}

				} else {
					//System.out.println("Not a valid Hello Message");
				}

			}
		}
	}

}
