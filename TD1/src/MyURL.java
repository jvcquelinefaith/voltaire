import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parsing the URL format
 * @author jackie.charles-etuk
 */
public class MyURL {
	static String dot = "\\.";
	static String alpha_nums = "\\p{Alnum}+";
	static String proto_regex = "(?<protocol>\\p{Alpha}+)";
	static String host_regex = "(?<host>(" + alpha_nums + dot + ")*" + alpha_nums + ")";
	static String port_regex = "(:(?<port>\\p{Digit}+))?";
	static String path_char = "[\\p{Graph}^[/?#]]";
	static String path_regex = "(?<path>(/(" + path_char + "+/)*" + path_char + "*))";
	static String full_regex = "(" + proto_regex + "://" + host_regex + port_regex + path_regex + ")";
	
	Pattern pattern;
	Matcher matcher;
	
	static String global_url = "";
		
	public MyURL(String url) throws IllegalArgumentException {
		pattern = Pattern.compile(full_regex);
		matcher = pattern.matcher(url);
		global_url = url;
		
		try {
			if (!matcher.find()) {
				throw new IllegalArgumentException();
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("This is not a valid URL.\n Please check your input and try again :)");
		}
	}
	
	public String getProtocol() {
		return matcher.group("protocol");
	}
	
	public String getHost() {
		return matcher.group("host");
	}
	
	public int getPort() {
		String port = matcher.group("port");
		if (port == null || port.isEmpty()) {
			port = "-1";
		}
		int integer_port = Integer.parseInt(port);
		return integer_port;
	}
	
	public String getPath() {
		return matcher.group("path");
	}


	public static void main(String[] args) {
		MyURL parser = new MyURL(args[0]);
		System.out.println("url : " + global_url);
		System.out.println("protocol : " + parser.getProtocol());
		System.out.println("host : " + parser.getHost());
		System.out.println("port : " + parser.getPort());
		System.out.println("path : " + parser.getPath());
		
	}

}
