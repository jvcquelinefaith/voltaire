import java.time.LocalTime;

public class Peer {
	private String peerId;
	private String peerIp;
	private String peerSeq;
	private LocalTime expirationTime;
	private String state;
	
	enum states { INCONSISTENT, HEARD, SYNCHRONISED, DYING };
	
	public Peer(String peerId, String peerSeq, LocalTime localTime, String state) {
		setPeerId(peerId);
		setPeerSeq(peerSeq);
		setExpirationTime(localTime);
		setState(state);
	}


	public String getPeerId() {
		return peerId;
	}


	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}


	public String getPeerIp() {
		return peerIp;
	}


	public void setPeerIp(String peerIp) {
		this.peerIp = peerIp;
	}


	public String getPeerSeq() {
		return peerSeq;
	}


	public void setPeerSeq(String peerSeq) {
		this.peerSeq = peerSeq;
	}


	public LocalTime getExpirationTime() {
		return expirationTime;
	}


	public void setExpirationTime(LocalTime localTime) {
		this.expirationTime = localTime;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}
	
	
	
}
