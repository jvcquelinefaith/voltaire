import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Xserver {
	static String dot = "\\.";
	static String alpha_nums = "\\p{Alnum}+";
	static String path_char = "[\\p{Graph}^[/?#]]";
	static String path_regex = "(?<path>(/(" + path_char + "+/)*" + path_char + "*))";
	static String get_regex = "GET " + path_regex + " HTTP/1.1";
	static String host_regex = "(?<host>(" + alpha_nums + dot + ")*" + alpha_nums + ")";

	static Pattern pattern;
	static Matcher matcher;

	public static void main(String[] args) { 
		if (args.length < 1) {
	      System.err.println("Usage: java Xserver proxyPort [nThreads] [backlog]");
	      System.exit(-1);
	    }
		int numThreads = 0;
		int backlog = 3;
		if (args.length > 1) {		
			numThreads = Integer.parseInt(args[1]);
		}
		if (args.length > 2) {
			backlog = Integer.parseInt(args[2]);
		}
		
		int portNumber = Integer.parseInt(args[0]);
		boolean runnable = true;
		if (numThreads > 0) {
			threadedRun(numThreads, portNumber, backlog);
		} else {
			try {
				ServerSocket server = new ServerSocket(portNumber, backlog);
				while (runnable) {
					Socket accepted = server.accept();
					System.out.println(
							"*************************\n" + "*Connected to SS Jackie!*\n" + "*************************");
					handleConnection(accepted);
				}
			} catch (Exception e) {
				//System.out.println("Server Exception: " + e.getMessage() + " caught!");
				e.printStackTrace();
			}
		}
	}
	
	public static void threadedRun(int numThreads, int portNumber, int backlog) {
		final ArrayList<Thread> pool = new ArrayList<Thread>();
		try {
			ServerSocket server = new ServerSocket(portNumber, backlog);
			while (pool.size() < numThreads) {
				System.out.println("creating threads");
				Thread thread = new Thread(new Runnable() {
					public void run() {
						while (!Thread.currentThread().isInterrupted()) {
							Socket accepted = null;
							try {
								accepted = server.accept();
								System.out.println(
										"*************************\n" + 
										"*Connected to SS Jackie!*\n" +
										"*My Name is: " + Thread.currentThread().getName() +
										"*\n*************************");
								handleConnection(accepted);
							} catch (IOException e) {
								System.out.println("Handler Exception: " + e.getMessage() + " caught!");
							}
						}
					}
				});
				thread.start();
				pool.add(thread);
			}
		} catch (IOException e) {
			System.out.println("Handler Exception: " + e.getMessage() + " caught!");
		}
	}

	public static void handleConnection(Socket socket) throws IOException {
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		String query;
		Boolean getQuery = false;
		Boolean getHost = false;
		
		String filePath = "";
		Boolean validFile = false;
		long contentLength = 0;
		
		FileReader fileReader = null;
		
		String statusCode = "";

		while ((query = in.readLine()) != null) {
			pattern = Pattern.compile(get_regex);
			matcher = pattern.matcher(query);
			if (matcher.find()) {
				getQuery = true;
				if (matcher.group("path") != null) {
					filePath = matcher.group("path");
				}
			}
			pattern = Pattern.compile(host_regex);
			matcher = pattern.matcher(query);
			if (matcher.find()) {
				getHost = true;
			}
			if (filePath.length() > 0) {
				filePath = filePath.substring(1, filePath.length());
			}
			File requestedFile = new File(filePath);
			validFile = requestedFile.exists() && requestedFile.canRead() && requestedFile.length() > 0;
			contentLength = requestedFile.length();
			
			if (getQuery && getHost && validFile) {
				statusCode = "200";
			} else if (filePath.isEmpty()) {
				statusCode = "404";
			} else if (!validFile) {
				statusCode = "404";
			} else {
				statusCode = "502";
			}
			
			//Send Header
			out.println("HTTP " + statusCode + "\n" + "Content-Length: " + contentLength + "\n" + "Accept-Ranges: bytes \n"
					+ "Connection: Keep-Alive \n" + "Content-Type: text/html; charset=utf-8\n" + "Date: "
					+ System.currentTimeMillis() + "\n" + "Server: Jackie's Server \n" + "Connection: persistent\n");
			
			//Send Content if 200
			if (statusCode.equals("200")) {
				try {
					fileReader = new FileReader(filePath);
					int ch = fileReader.read();
				    while(ch != -1) {
				        out.print((char)ch);
				        ch = fileReader.read();
				    }
				} catch (FileNotFoundException e) {
					System.out.println("Handler Exception: " + e.getMessage() + " caught!");
					statusCode = "404";
	
				} catch (IOException e) {
					System.out.println("Handler Exception: " + e.getMessage() + " caught!");
					statusCode = "502";
				}
			}
			
		}
		
		if (fileReader != null) {
			fileReader.close();
		}
		out.close();
	}

}
