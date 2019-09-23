import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Xurl {
	
	Socket xSocket;
	BufferedReader reader;
	PrintWriter writer;
	PrintStream resultFile;
	String downloadPath = "";
	
	public Xurl(String host, int port, String path) {
		//Setting default port to 80 if not specified
		if (port < 0) {
			port = 80;
		}
		openConnection(host, port);
		downloadPath = path;
	}
	
	public void openConnection(String host, int port) {
		try {
			xSocket = new Socket(host, port);
			reader = new BufferedReader(new InputStreamReader(xSocket.getInputStream()));
			writer = new PrintWriter(xSocket.getOutputStream(), true);
			
			//Creating and setting output destination
			resultFile = new PrintStream("./output.out");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("connection open.");
	}
	
	public void processCommand() {
		System.out.println("processing commands...");
		try {
			String command;
			
			//Issuing command to the socket
			writer.println("GET " + downloadPath + " HTTP/1.1\r\n");
			System.out.println("check out a file called output.out in this directory.");
			
			//Processing the issued command
			while ((command = reader.readLine()) != null) {
				
				//Checking for non-OK HTTP codes
				if (command.contains("HTTP/1.1 301")) {
					System.err.println("Moved Permanently");
				} else if (command.contains("HTTP/1.1 302")) {
					System.err.println("Moved Temporarily");
				} else if (command.contains("HTTP/1.1 400")) {
					System.err.println("Bad Request J");
				} else if (command.contains("HTTP/1.1 404")) {
					System.err.println("Not Found");
				}
				System.out.println(command);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("process complete.");
	}

	public void closeConnection() {
		try {
			xSocket.close();
			reader.close();
			writer.close();
			resultFile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("connection closed.");
	}
	

	public static void main(String[] args) {
		MyURL parser = new MyURL(args[0]);
		String host = parser.getHost();
		int port = parser.getPort();
		String path = parser.getPath();
		Xurl x = new Xurl(host, port, path);
		x.processCommand();
		x.closeConnection();
	}

}
