package pro.delfik.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Coder {
	public static String eMap(Map<String, String> map){
		if(map == null)return "";
		StringBuilder buffer = new StringBuilder();
		for(Map.Entry<String, String> entry : map.entrySet())
			buffer.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
		return buffer.toString();
	}

	public static Map<String, String> dMap(String str){
		if(str == null)return new HashMap<>();
		String split[] = str.split("\n");
		Map<String, String> map = new HashMap<>();
		for(String line : split){
			String spl[] = line.split("=");
			if(spl.length == 1) map.put(spl[0], "");
			else map.put(spl[0], spl[1]);
		}
		return map;
	}

	public static String eList(List<String> list){
		if(list == null)return "";
		StringBuilder buffer = new StringBuilder();
		for(String line : list)
			buffer.append(line).append('\n');
		return buffer.toString();
	}

	public static List<String> dList(String str){
		if(str == null)return new ArrayList<>();
		String split[] = str.split("\n");
		List<String> list = new ArrayList<>(split.length);
		for(String line : split)
			list.add(line);
		return list;
	}
}
