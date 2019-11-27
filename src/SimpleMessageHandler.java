public interface SimpleMessageHandler {
	
	public void setMuxDemux(MuxDemux md);
	
	public void handleMessage(String m);

}
