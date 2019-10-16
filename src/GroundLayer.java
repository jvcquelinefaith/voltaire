import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class GroundLayer {

	/**
	 * This {@code Charset} is used to convert between our Java native String
	 * encoding and a chosen encoding for the effective payloads that fly over the
	 * network.
	 */
	private static final Charset CONVERTER = StandardCharsets.UTF_8;
	private static DatagramSocket socket = null;
	private static byte[] sendBuffer = new byte[256];
	private static byte[] recieveBuffer = new byte[256];
	private static boolean runnable = true;
	private static Layer destLayer = null;

	/**
	 * This value is used as the probability that {@code send} really sends a
	 * datagram. This allows to simulate the loss of packets in the network.
	 */
	public static double RELIABILITY = 1.0;

	public static boolean start(int localPort) {
		try {
			socket = new DatagramSocket(localPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		while (!Thread.currentThread().isInterrupted() && runnable) {
			Thread thread = new Thread(new Runnable() {
				public void run() {
					DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						runnable = false;
						System.err.println(e.getMessage());
					}

				}
			});
			thread.start();
		}
		return socket != null && runnable;
	}

	public static void deliverTo(Layer layer) {
		if (layer != null) {
			destLayer = layer;
		}
	}

	public static void send(String payload, String destinationHost, int destinationPort) {
		//payload.
		double l = Math.random();
		if (l != RELIABILITY) {
			InetAddress destinationAddress;
			try {
				destinationAddress = InetAddress.getByName(destinationHost);
				DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, destinationAddress, destinationPort);
				socket.send(sendPacket);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public static void close() {
		Thread.currentThread().interrupt();
		socket.close();
		System.err.println("GroundLayer closed");
	}

}
