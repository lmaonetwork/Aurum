package pro.delfik.proxy.skins;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ProxyManager {
	public static List<String> proxies = new ArrayList<>();
	private static String inputLine;
	
	public static List<String> getList() {
		if (proxies.isEmpty()) {
			System.out.print("[SkinsRestorer] Proxy list is empty. Getting one.");
			return updateProxies();
		}
		return proxies;
	}
	
	public static List<String> updateProxies() {
		proxies = new ArrayList<>();
		String url = "https://getmeproxy.com/api/v1.0/api.php?key=c84d1076312bcf1e875c94d4e20692f5&checked=1&s=5&list=text";
		proxies = readURL(url);
		return proxies;
	}
	
	private static List<String> readURL(String url) {
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
			
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "SkinsRestorer");
			con.setConnectTimeout(5000);
			con.setReadTimeout(5000);
			con.setDoOutput(true);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("UTF-8")));
			String str = "";
			int limit = 5;
			while ((inputLine = in.readLine()) != null) {
				str = inputLine;
			}
			String[] asd = str.split("<br>");
			for (String d : asd) {
				if (limit == 0) {
					break;
				}
				proxies.add(d);
				limit--;
			}
			in.close();
			return proxies;
		} catch (IOException e) {
			System.out.print("[SkinsRestorer] We couldn't update the proxy list. This usually indicates a firewall problem. A detailed error is below.");
			e.printStackTrace();
		}
		return updateProxies();
	}
}
