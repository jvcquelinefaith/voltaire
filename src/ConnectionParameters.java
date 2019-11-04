public class ConnectionParameters {
	private final int rId;
	private final String rHost;
	private final int rPort;

	public ConnectionParameters(int remoteId, String remoteHost, int remotePort) {
		this.rId = remoteId;
		this.rHost = remoteHost;
		this.rPort = remotePort;
	}

	public int getRemoteId() {
		return this.rId;
	}

	public String getRemoteHost() {
		return this.rHost;
	}

	public int getRemotePort() {
		return this.rPort;
	}

	@Override
	public String toString() {
		return '[' + this.rId + '@' + this.rHost + ':' + this.rPort + ']';
	}
}
