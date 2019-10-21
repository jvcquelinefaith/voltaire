import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
	private static byte[] sendbuffer = null;
	private static byte[] recieveBuffer = null;
	private static Layer destLayer = null;
	private static String currLayer = null;
	private static InetAddress destinationAddress;
	private static Thread thread;
	private static boolean runnable = false;

	/**
	 * This value is used as the probability that {@code send} really sends a
	 * datagram. This allows to simulate the loss of packets in the network.
	 */
	public static double RELIABILITY = 1.0;

	public static boolean start(int localPort) {
		try {
			socket = new DatagramSocket(localPort);
			runnable = true;
		} catch (SocketException e) {
			e.printStackTrace();
		}
		thread = new Thread(new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted() && runnable) {
					recieveBuffer = new byte[1000];
					DatagramPacket packet = new DatagramPacket(recieveBuffer, recieveBuffer.length);
					try {
						socket.receive(packet);
						if (destLayer != null) {
							// inspired by https://www.baeldung.com/udp-in-java
							String received = new String(packet.getData(), 0, packet.getLength(),CONVERTER);
							currLayer = this.getClass().getName();
							destLayer.receive(received, currLayer.subSequence(0, currLayer.length()-2).toString());
						}
					} catch (IOException e) {
						runnable = false;
						System.err.println(e.getMessage());
						Thread.currentThread().interrupt();
					}
				}
			}
		});
		thread.start();
		return runnable;
	}

	public static void send(String payload, String destinationHost, int destinationPort) {
		int size = payload.getBytes(CONVERTER).length;
		sendbuffer = new byte[size];
		sendbuffer = payload.getBytes(CONVERTER);
		double l = Math.random();
		if (l != 1-RELIABILITY) {
			try {
				destinationAddress = InetAddress.getByName(destinationHost);
				DatagramPacket packet = new DatagramPacket(sendbuffer, sendbuffer.length, destinationAddress, destinationPort);
				socket.send(packet);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	public static void deliverTo(Layer above) {
		if (above != null) {
			destLayer = above;
		} else {
			destLayer = null;
		}
	}

	public static void close() {
		thread.interrupt();
		Thread.currentThread().interrupt();
		try {
			thread.join();
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		}
		socket.close();
		System.out.println("GroundLayer closed");	
	}

}
