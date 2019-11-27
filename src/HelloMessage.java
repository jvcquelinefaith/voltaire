import java.util.ArrayList;

public class HelloMessage {
	private String senderID;
	private String sequenceNo;
	private int helloInterval;
	private int numPeers;
	private ArrayList<String> peers = new ArrayList<String>();
	
	public HelloMessage(String s) {
		String[] message = s.split(";");
		this.senderID = message[1];
		this.sequenceNo = message[2];
		this.helloInterval = Integer.parseInt(message[3]);
		this.numPeers = Integer.parseInt(message[4]);
		System.out.println(message);
		if (message.length > 5) {
			this.peers.add(message[5]);	
		}
	}
	
	public HelloMessage(String senderID, String sequenceNo, int helloInterval) {
		this.senderID = senderID;
		this.sequenceNo = sequenceNo;
		this.helloInterval = helloInterval;
		numPeers = 0;
		peers = new ArrayList<String>();
	}
	
	public String getHelloMessageAsEncodedString() {
		String peersString = "";
		String message = "";
		if (peers != null) {
			for (String s : peers) {
				peersString += ";" + s;
			}
			message = "HELLO;" + senderID + ";" + sequenceNo + ";" +
					helloInterval + ";" + numPeers + peersString;
		} else {
			message = "HELLO;" + senderID + ";" + sequenceNo + ";" +
					helloInterval + ";0";
		}
		return message;
	}
	
	public void addPeer(String peerID) {
		if(!peers.contains(peerID)) {
			peers.add(peerID);
			numPeers += 1;
		}
	}
	
	public void removePeer(String peerID) {
		if(peers.contains(peerID)) {
			peers.remove(peerID);
			numPeers -= 1;
		}
	}
	
	public String toString() {
		String peersString = "";
		String readable = "";
		if (peers != null ) {
			for (String s : peers) {
				peersString += s + ", ";
			}
			readable = "HELLO from: " + senderID + ", with sequenceNo: " 
					+ sequenceNo + " and Hello Interval: " + helloInterval 
					+ " and " + numPeers + " peers called " + peersString;
		} else {
			readable = "HELLO from: " + senderID + ", with sequenceNo: " 
					+ sequenceNo + ", Hello Interval: " + helloInterval 
					+ " and " + numPeers + " peers.";
		}
		return readable;
	}
	
	public String getSenderID() {
		return senderID;
	}

	public void setSenderID(String senderID) {
		this.senderID = senderID;
	}

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public int getHelloInterval() {
		return helloInterval;
	}

	public void setHelloInterval(int helloInterval) {
		if (helloInterval <= 255 && helloInterval >= 0) {
			this.helloInterval = helloInterval;
		}
	}

	public int getNumPeers() {
		return numPeers;
	}

	public void setNumPeers(int numPeers) {
		if (numPeers <= 255 && numPeers >= 0) {
			this.numPeers = numPeers;
		}
	}

	public ArrayList<String> getPeers() {
		return peers;
	}

	public void setPeers(ArrayList<String> peers) {
		this.peers = peers;
	}
	
	public static void main(String[] args) {
		HelloMessage h = new HelloMessage("HELLO;Bob;42;60;0");
		System.out.println(h);
	}
}

