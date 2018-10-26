package pro.delfik.proxy.modules;

import implario.util.ByteUnzip;
import implario.util.ByteZip;
import implario.util.Coder;
import implario.util.StringUtils;
import pro.delfik.proxy.Aurum;
import pro.delfik.proxy.data.DataIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Chat {
	private static final Pattern uncorrectChars = Pattern.compile
			("[^A-Za-z0-9А-я\"'!?.,()@#№$%:\\^\\*\\&\\-\\_=+~`\\[\\]\\{\\}\\|\\/<>;Ёё]");

	private static final String[] nya = new String[]{"мяу", "meow", "меов", "ня", "nya", "капибара", "шмыг", "апчхи", "кхе"};

	private static final char[] unMatChars = "\"'!?.,()@#№$%:^*&123457890-_=+~`[]{}\\|/<".toCharArray();

	private static final HashMap<Character, Character> map = new HashMap<>();

	private static final List<String> mat = new ArrayList<>();

	static {
		List<String> mats = DataIO.read("info/mat");
		if(mats != null) mat.addAll(mats);
		Aurum.addUnload(() -> DataIO.write("info/mat", mat));
		map.put('6', 'б');
		map.put('k', 'к');
		map.put('a', 'а');
		map.put('o', 'о');
		map.put('s', 'с');
		map.put('c', 'с');
		map.put('b', 'ь');
		map.put('y', 'у');
		map.put('p', 'р');
		map.put('g', 'д');
		map.put('m', 'м');
		map.put('z', 'з');
		map.put('r', 'р');
		map.put('d', 'д');
		map.put('e', 'е');
		map.put('t', 'т');
		map.put('x', 'х');
		map.put('l', 'л');
		map.put('n', 'п');
	}

	public static void addMat(String mat){
		Chat.mat.add(remake(mat));
	}

	public static void remMat(String mat){
		Chat.mat.remove(remake(mat));
	}

	public static boolean isMat(String mat){
		return mat.contains(remake(mat));
	}

	//Нужна синхронность что бы была
	public synchronized static String removeUncorrectChars(String message){
		return uncorrectChars.matcher(message).replaceAll("");
	}

	public static String removeTrippleChars(String message){
		StringBuilder sb = new StringBuilder();
		char last = (char)4920, lastLast = (char)4920;
		for(char c : message.toCharArray()) {
			if (c == last && last == lastLast)continue;
			lastLast = last;
			last = c;
			sb.append(c);
		}
		return sb.toString();
	}

	public static String getNya() {
		return '*' + nya[(int)(Math.random() * nya.length)] + '*';
	}

	private static String remake(String in) {
		StringBuilder result = new StringBuilder();
		char last = 4920;
		for(char c : in.toCharArray()){
			if(last == c || StringUtils.contains(unMatChars, c)) continue;
			Character local = map.get(c);
			c = local == null ? c : local;
			last = c;
			result.append(c);
		}
		return result.toString().toLowerCase();
	}

	public static String applyMat(String message) {
		StringBuilder sb = new StringBuilder();
		String last = "";

		for(String result : message.split(" ")) {
			result = removeUncorrectChars(result);
			String remake = remake(result).toLowerCase();
			if (remake.length() != 0) {
				if (mat.contains(remake + " ")) {
					sb.append(last);
					sb.append(' ');
					sb.append(getNya());
					last = "";
					continue;
				}

				if (mat.contains(remake(last).toLowerCase() + remake + " ")) {
					sb.append(getNya());
					last = "";
					continue;
				}
			}

			sb.append(last);
			sb.append(' ');
			last = result;
		}

		sb.append(last);
		return removeTrippleChars(sb.toString()).substring(1);
	}
}
