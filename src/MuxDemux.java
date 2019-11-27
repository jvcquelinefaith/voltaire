import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

public class MuxDemux implements Runnable {
	private DatagramSocket mySocket = null;
	private SimpleMessageHandler[] myMessageHandlers;
	private SynchronousQueue<String> outgoing = new SynchronousQueue<String>();
	final HashMap<String, Peer> peersList = new HashMap<String, Peer>();
	private String senderID = "";

	public MuxDemux(SimpleMessageHandler[] h) {
		try {
			mySocket = new DatagramSocket(4242);
			mySocket.setBroadcast(true);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		myMessageHandlers = h;
	}

	public static void main(String[] args) {
		SimpleMessageHandler[] handlers = new SimpleMessageHandler[3];
//		HelloHandler handler = new HelloHandler();
		HelloSender sender = new HelloSender();
		HelloReceiver receiver = new HelloReceiver();
		DebugReceiver debugger = new DebugReceiver();
//		handlers[0] = handler;
		handlers[0] = sender;
		handlers[1] = receiver;
		handlers[2] = debugger;
		MuxDemux dm = new MuxDemux(handlers);
		new Thread((Runnable) handlers[0]).start();
		new Thread((Runnable) handlers[1]).start();
		new Thread((Runnable) handlers[2]).start();
		new Thread(dm).start();
	}

	@Override
	public void run() {
		for (int i = 0; i < myMessageHandlers.length; i++) {
			myMessageHandlers[i].setMuxDemux(this);
		}
		while (true) {
			try {
				byte[] buffer = new byte[16384];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				mySocket.receive(packet);
				buffer = packet.getData();
				String payload = new String(buffer, 0, packet.getLength());
				if (payload != null) {
					for (int i = 0; i < myMessageHandlers.length; i++) {
						myMessageHandlers[i].handleMessage(payload);
					}
				}
			} catch (IOException e) {
				break;
			}
		}
		mySocket.close();
	}

	public void send(String s) {
		if (mySocket != null) {      
			byte[] buffer = s.getBytes();
			DatagramPacket dp;
			try {
				dp = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 4242);
				mySocket.send(dp);
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	public void setMyName(String myName) {
		senderID = myName;
	}
	
	public String getMyName() {
		return senderID;
	}

}
