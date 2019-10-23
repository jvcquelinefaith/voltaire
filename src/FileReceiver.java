import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FileReceiver {
	private String senderHost;
	private int senderPort;
	private int senderId;

	public FileReceiver(String clientHost, int clientPort, int id) {
		senderHost = clientHost;
		senderPort = clientPort;
		senderId = id;
	}
	
	public void write(String payload) {
		PrintWriter writer = null;
		if (payload.contains("SEND")) {
			String[] message = payload.split(" ");
			String filename = message[1];
			String newFilename = "_recieved_" + filename;
			try {
				writer = new PrintWriter(newFilename);
			} catch (FileNotFoundException e) {
				System.err.println(e.getMessage());
			}
		} 
		if (writer != null) {
			writer.println(payload);
		}
	}
	
	public void receive(String payload, String source) {
		
	}

	public void close() {

	}
}
