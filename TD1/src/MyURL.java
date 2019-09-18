import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsing the URL format
 * @author jackie.charles-etuk
 */
public class MyURL {
	
	private static String global_url = "";
	static String dot = "\\.";
	static String alpha_nums = "\\p{Alnum}+";
	static String proto_regex = "(?<protocol>\\p{Alpha}+)";
	static String host_regex = "(?<host>(" + alpha_nums + dot + ")*" + alpha_nums + ")";
	static String port_regex = "(:(?<port>\\p{Digit}+))?";
	static String path_char = "[\\p{Graph}^[/?#]]";
	static String path_regex = "(?<path>(/(" + path_char + "+/)*" + path_char + "*))";
	static String full_regex = "(" + proto_regex + "://" + host_regex + port_regex + path_regex + ")";
	
	static Pattern pattern;
	static Matcher matcher;
		
	public MyURL(String url) throws IllegalArgumentException {
		global_url = url;	
		pattern = Pattern.compile(full_regex);
		matcher = pattern.matcher(global_url);
		try {
			if (!matcher.find()) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("This is not a valid URL.\n Please check your input and try again :)");
		}
	}
	
	public String getProtocol() {
		try {
			return matcher.group("protocol");
		} catch (Exception e) {
			throw new IllegalArgumentException("Please input a valid protocol");
		}
	}
	
	public String getHost() {
		try {
			return matcher.group("host");
		} catch (Exception e) {
			throw new IllegalArgumentException("Please input a valid host");
		}
	}
	
	public String getPort() {
		String port = matcher.group("port");
		if (port != null && !port.isEmpty()) {
			return port;
		} else {
			port = "-1";
		}
		return port;
	}
	
	public String getPath() {
		try {
			return matcher.group("path");
		} catch (Exception e) {
			throw new IllegalArgumentException("Please input a valid path");
		}
	}


	public static void main(String[] args) {
		MyURL parser = new MyURL(args[0]);
		
		System.out.println("protocol : " + parser.getProtocol());
		System.out.println("host : " + parser.getHost());
		System.out.println("port : " + parser.getPort());
		System.out.println("path : " + parser.getPath());
		
	}

}
