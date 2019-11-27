
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
					int remote_id = parameters.getRemoteId();
					int local_id = (int) (Math.random() * Integer.MAX_VALUE);
					System.out.println("about to FR with host: " + host + " port: " + port + " id: " + local_id);
					FileReceiver receiver = new FileReceiver(host, port, local_id);
					Layer connected = receiver.getSubLayer();
//					connected.receive(local_id + ";0;--HELLO--", "/" + host + ":"+ port );
					QueueingDispatchLayer.register(connected, remote_id);
					receiver.close();
					GroundLayer.close();
			    }
			}
		}
	}
}
