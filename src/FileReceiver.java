import java.io.FileWriter;
import java.io.IOException;

public class FileReceiver implements Layer {
	private Layer subLayer;
	private boolean isCloseable = false;
	private FileWriter writer = null;
	private String newFilename = "";	

	public FileReceiver(String clientHost, int clientPort, int id) {
		this.subLayer = new ConnectedLayer(clientHost, clientPort, id);
		this.subLayer.deliverTo(this);
	}
	
	public boolean getCloseable() {
		return isCloseable;
	}
	
	public Layer getSubLayer() {
		return subLayer;
	}
	
	@Override
	public void receive(String payload, String source) {
		if (payload.equals("**CLOSE**")) {
			isCloseable = true;
			close();
		} else if (payload.contains("SEND")) {
			String[] message = payload.split(" ");
			String filename = message[1];
			newFilename = "_received_" + filename;
			try {
				writer = new FileWriter(newFilename);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		} else {
			send(payload);
		}
		
	}

	@Override
	public void close() {
		if (isCloseable) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public void send(String payload) {
		if (writer != null) {
			try {
				//System.out.println("writing");
				writer.write(payload + '\n');
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	@Override
	public void deliverTo(Layer above) {
		throw new UnsupportedOperationException("don't support any Layer above");
	}
}
