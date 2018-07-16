package pro.delfik.proxy.data;

import net.md_5.bungee.BungeeCord;
import pro.delfik.util.Converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataPort {
	private static List<String> motds;
	private static List<Integer> port;
	
	public DataPort() {
	}
	
	public static void unload() {
		Map<String, String> write = new HashMap<>();
		
		for(int i = 0; i < motds.size(); ++i) {
			write.put(motds.get(i), port.get(i) + "");
		}
		
		DataIO.writeConfig("config/ports", write);
	}
	
	public static int putPort(String ip) {
		if (motds.contains(ip)) {
			return port.get(motds.indexOf(ip));
		} else {
			for(int i = 40000; i < 65534; ++i) {
				if (!port.contains(i)) {
					motds.add(ip);
					port.add(i);
					return i;
				}
			}
			
			BungeeCord.getInstance().stop("Видимо на сервере завелась мамка лелла, сожравшая на завтрак все доступные порты. Перегазагрузите банж. Также советую нанять киллера. Армию киллеров. (Мамке же нужно чем-то питаться)");
			return -1;
		}
	}
	
	public static void remPort(String motd) {
		int i = motds.indexOf(motd);
		if (i != -1) {
			motds.remove(i);
			port.remove(i);
		}
	}
	
	public static int getPort(String motd) {
		int i = motds.indexOf(motd);
		return i == -1 ? -1 : port.get(i);
	}
	
	static {
		Map<String, String> read = DataIO.readConfig("config/ports");
		if (read == null) {
			motds = new ArrayList<>();
			port = new ArrayList<>();
		} else {
			ArrayList<String> list = new ArrayList<>();
			list.addAll(read.keySet());
			
			motds = list;
			port = new ArrayList<>();
			
			for (String line : read.values()) {
				port.add(Converter.toInt(line));
			}
		}
		
	}
}
