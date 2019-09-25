import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLprocessing {
	
	static String dot = "\\.";
	static String alpha_nums = "\\p{Alnum}+";
	static String proto_regex = "(?<protocol>\\p{Alpha}+)";
	static String host_regex = "(?<host>(" + alpha_nums + dot + ")*" + alpha_nums + ")";
	static String port_regex = "(:(?<port>\\p{Digit}+))?";
	static String path_char = "[\\p{Graph}^[/?#]]";
	static String path_regex = "(?<path>(/(" + path_char + "+/)*" + path_char + "*))";
	static String full_regex = "([\"'](?<url>(" + proto_regex + "://" + host_regex + port_regex + path_regex + "))[\"'])";
	
	static String href_regex = "(a(^a/./)+href)";
	
	static Pattern pattern;
	static Matcher matcher;
	
	public interface URLhandler {
		void takeUrl(String url);
	}

	public static URLhandler handler = new URLhandler() {
		public void takeUrl(String url) {
			System.out.println(url);
		}
	};

	/**
	 * Parse the given buffer to fetch embedded links and call the handler to
	 * process these links.
	 * 
	 * @param data
	 * the buffer containing the http document
	 */
	public static void parseDocument(CharSequence data) {
		int count = 0;
		String line = "";
		char current;
		String valid_url = "";
		while (count < data.length()) {
			current = data.charAt(count);
			line += current;
			if(current == '>') {
				pattern = Pattern.compile(href_regex, Pattern.CASE_INSENSITIVE);
				matcher = pattern.matcher(line);
				if (matcher.find()) {
					pattern = Pattern.compile(full_regex);
					matcher = pattern.matcher(line);
					if(matcher.find()) {
						valid_url = matcher.group("url");
						handler.takeUrl(valid_url);
					}
				}
				line = "";
			}
			count++;
		}
	}
}
