import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelloSender implements SimpleMessageHandler, Runnable {
	private MuxDemux muxDemux;

	private String hello = "(HELLO)";
	private String colon = "(;)";
	private String senderID = "(^[a-zA-Z0-9]{1,16}$)";
	private String sequence = "([-]*[0-9]+)";
	private String hello_interval = "([0-255]+)";
	private String num_peers = "([0-255]+)";
	private String peers = "([A-Za-z0-9]{1,16})*";
	private String regex = hello + colon + senderID + colon + sequence + colon + hello_interval + colon + num_peers
			+ "(" + colon + ")*" + peers;

	private Pattern pattern;
	private Matcher matcher;

	private String newPeer = "";
	private String myName = "";

	@Override
	public void setMuxDemux(MuxDemux md) {
		muxDemux = md;
	}

	@Override
	public void handleMessage(String m) {
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(m);

		if (matcher.find()) {
			String[] msg = m.split(";");
			newPeer = msg[1];
		}
	}

	@Override
	public void run() {
		myName = "Jackie";
		HelloMessage hello = new HelloMessage(myName, "1", 20);
		muxDemux.setMyName(myName);
		
		while (true) {
			if (!newPeer.isEmpty()) {
				synchronized(muxDemux.peersList) {
					if (muxDemux.peersList.containsKey(newPeer) && muxDemux.peersList.get(newPeer) != null) {
						Peer p = muxDemux.peersList.get(newPeer);
						hello.addPeer(newPeer);
						hello.setSequenceNo(p.getPeerSeq());
//						System.out.println("adding");
					} else {
						hello.removePeer(newPeer);
//						System.out.println("removing");
					}
				}
			}
			String coded = hello.getHelloMessageAsEncodedString();
			muxDemux.send(coded);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
