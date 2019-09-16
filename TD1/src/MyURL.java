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
	static String proto_regex = "\\p{Alpha}+";
	static String host_regex = "(" + alpha_nums + dot + ")*" + alpha_nums;
	static String port_regex = "(:\\p{Digit}+)?";
	static String path_char = "[\\p{Graph}^[/?#]]";
	static String path_regex = "/(" + path_char + "+/)*" + path_char + "*";
	static String full_regex = "(" + proto_regex + "://" + host_regex + port_regex + path_regex + ")";
	
	static Pattern pattern;
	static Matcher matcher;
	
	static int previous_index = 0;
	
	public static void parse(String url) throws IllegalArgumentException {
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
		System.out.println("url : " + global_url);
		System.out.println("protocol : " + getProtocol());
		System.out.println("host : " + getHost());
		System.out.println("port : " + getPort());
		System.out.println("path : " + getPath());
	
	}
	
	public static String getProtocol() {
		pattern = Pattern.compile(proto_regex);
		matcher = pattern.matcher(global_url);
		try {
			return getMatch();
		} catch (Exception e) {
			throw new IllegalArgumentException("Please input a valid protocol");
		}
	}
	
	public static String getHost() {
		pattern = Pattern.compile(host_regex);
		matcher = pattern.matcher(global_url);
		try {
			return getMatch();
		} catch (Exception e) {
			throw new IllegalArgumentException("Please input a valid host");
		}
	}
	
	public static String getPort() {
		pattern = Pattern.compile(port_regex);
		matcher = pattern.matcher(global_url);
		String port = getMatch();
		if (!port.isEmpty()) {
			String[] portList = port.split(":");
			port = portList[1];
		} else {
			port = "-1";
		}
		return port;
	}
	
	public static String getPath() {
		pattern = Pattern.compile(path_regex);
		matcher = pattern.matcher(global_url);
		try {
			return getMatch();
		} catch (Exception e) {
			throw new IllegalArgumentException("Please input a valid path");
		}
	}
	
	public static String getMatch() {
		if (matcher.find(previous_index)) {
			previous_index = matcher.end();
			return matcher.group();
		} else {
			previous_index = matcher.end();
			throw new IllegalArgumentException();
		}
	}

	public static void main(String[] args) {
		parse(args[0]);
	}

}
