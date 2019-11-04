
public class Server_6 {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("syntax : java Server_6 myPort");
			return;
		}
		if (GroundLayer.start(Integer.parseInt(args[0]))) {
			QueueingDispatchLayer.start();
			while(true) {
			    ConnectionParameters parameters = QueueingDispatchLayer.accept();
			    if (parameters != null) {
					String host = parameters.getRemoteHost();
					int port = parameters.getRemotePort();
					int id = parameters.getRemoteId();
					FileReceiver receiver = new FileReceiver(host, port, id);
					Layer connected = receiver.getSubLayer();
					connected.receive(parameters.getRemoteId() + ";0;--HELLO--", "/" + host + ":"+ port );
					QueueingDispatchLayer.register(connected, id);
					receiver.close();
					GroundLayer.close();
			    }
			}
		}
	}
}
