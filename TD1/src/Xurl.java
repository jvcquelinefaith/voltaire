import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Xurl {
	
	Socket xSocket;
	BufferedReader reader;
	PrintStream writer;
	PrintStream resultFile;
	String xPath = "";
	String xHost = "";
	String xFilename = "";
	int xContentLength = 0;
	
	public Xurl(String host, int port, String path) {
		xSocket = new Socket();
		//Setting default port to 80 if not specified
		if (port < 0) {
			port = 80;
		}
		xPath = path;
		xHost = host;
		xFilename = getFilename(path);
		openConnection(host, port);
	}
	
	public String getFilename(String path) {	
		String filename;
		String[] splitPath = path.split("/");		
		if (splitPath != null && splitPath.length > 0) {
			filename = splitPath[splitPath.length-1];
		} else {
			filename = "index";
		}
		return filename;
	}
	
	public void openConnection(String host, int port) {
		try {
			xSocket.connect(new InetSocketAddress(host, port), 10000);
			reader = new BufferedReader(new InputStreamReader(xSocket.getInputStream()));
			writer = new PrintStream(xSocket.getOutputStream(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("connection open.");
	}
	
	public int processHeader(String line) throws IOException {
		System.out.println("processing header...");		
		int http_status = 200;
		while (((line = reader.readLine()) != null) && !line.isEmpty()) {
			//Checking for non-OK HTTP codes
			if (line.contains("HTTP/1.1 301")) {
				System.err.println("301 Moved Permanently");
				http_status = 301;
			} else if (line.contains("HTTP/1.1 302")) {
				System.err.println("302 Moved Temporarily");
				http_status = 302;
			} else if (line.contains("HTTP/1.1 400")) {
				System.err.println("400 Bad Request");
				http_status = 400;
			} else if (line.contains("HTTP/1.1 404")) {
				System.err.println("400 Not Found");
				http_status = 404;
			}				
			if(line.contains("Content-Length")) {
				String content_length = line.split(":")[1].trim();
				xContentLength = Integer.parseInt(content_length);
			}
		}
		return http_status;
	}
	
	public void writeContent(String line) throws IOException {
		System.out.println("downloading file...");
		//Creating and setting output destination
		resultFile = new PrintStream(new File(xFilename));
		if (xContentLength > 0) {
			while (xContentLength > 0) {
				line = reader.readLine();
				resultFile.println(line);
				xContentLength -= line.length()+1;
			}
		} else {
			while((line = reader.readLine()) != null) {
				resultFile.println(line);
			}
		}
		resultFile.close();
	}
	
	public void processCommand() {
		System.out.println("processing commands...");
		try {			
			
			String command = "";
			//Issuing command to the socket
			writer.print("GET " + xPath + " HTTP/1.1\r\n");
			writer.print("Host: " + xHost + " \r\n");
			writer.print("\r\n");
			
			int http_status = processHeader(command);
			
			if (http_status == 200) {
				writeContent(command);
			} else if (http_status == 301 || http_status == 302) {
				System.err.println("Sorry this file redirects and we can't be bothered! Sorry!");
				return;
			} else if (http_status > 400) {
				System.err.println("Sorry this file either doesn't exist or your request was trash! Sorry!");
				return;
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("process complete.");
	}

	public void closeConnection() {
		try {
			xSocket.close();
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("connection closed.");
	}
	

	public static void main(String[] args) {
		if (args.length > 0) {
			MyURL parser = new MyURL(args[0]);
			String host = parser.getHost();
			int port = parser.getPort();
			String path = parser.getPath();
			Xurl x = new Xurl(host, port, path);
			x.processCommand();
			x.closeConnection();
		} else {
			System.err.println("Please input a valid url as argument!");
			return;
		}
	}

}

