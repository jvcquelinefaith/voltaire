import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
		int backlog = 3;
		int portNumber = Integer.parseInt(args[0]);
		boolean runnable = true;
		try {
			ServerSocket server = new ServerSocket(portNumber, backlog);			
			while (runnable) {
				Socket accepted = server.accept();
				System.out.println("*************************\n" +
								   "*Connected to SS Jackie!*\n" +
								   "*************************");
				handleConnection(accepted);
			}
		} catch (Exception e) {
			System.out.println("Exception caught");
		}
	}
	
	public static void handleConnection(Socket socket) throws IOException {
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));		

		String query;
		Boolean getQuery = false;
		Boolean getHost = false;
		String statusCode = "";
		
		while ((query = in.readLine()) != null) {
			pattern = Pattern.compile(get_regex);
			matcher = pattern.matcher(query);
			if(matcher.find()) {
				getQuery = true;
			}
			pattern = Pattern.compile(host_regex);
			matcher = pattern.matcher(query);
			if(matcher.find()) {
				getHost = true;
			}
			if (getQuery && getHost) {
				statusCode = "200";
				out.println("HTTP " + statusCode + "\n" +
						"Content-Length: 10 \n"+
						"Accept-Ranges: bytes \n" +
						"Connection: Keep-Alive \n" +
						"Content-Type: text/html; charset=utf-8\n" +
						"Date: " + System.currentTimeMillis() + "\n" +
						"Server: Jackie's Server \n");
			} else {
				statusCode = "502";
				out.println("HTTP " + statusCode + "OK\n" +
						"Content-Length: 10 \n"+
						"Accept-Ranges: bytes \n" +
						"Connection: Keep-Alive \n" +
						"Content-Type: text/html; charset=utf-8\n" +
						"Date: " + System.currentTimeMillis() + "\n" +
						"Server: Jackie's Server \n");
			}
			
		}
		
		

		
		out.close();
		socket.close();
	}

}
