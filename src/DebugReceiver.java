public class DebugReceiver implements SimpleMessageHandler, Runnable {
	private MuxDemux muxDemux;

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void setMuxDemux(MuxDemux md) {
		muxDemux = md;
	}

	@Override
	public void handleMessage(String m) {
		//System.out.println("DEBUG received: " + m);
	}

}
