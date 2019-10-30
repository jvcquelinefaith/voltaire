public class Server_4 {

	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("syntax : java Server_4 myPort clientHost clientPort");
			return;
		}
		if (GroundLayer.start(Integer.parseInt(args[0]))) {
			// GroundLayer.RELIABILITY = 0.5;
			FileReceiver receiver = new FileReceiver(args[1], Integer.parseInt(args[2]),
					(int) (Math.random() * Integer.MAX_VALUE));
			if (receiver.getCloseable()) {
				receiver.close();
				GroundLayer.close();
			}
		}
	}
}
