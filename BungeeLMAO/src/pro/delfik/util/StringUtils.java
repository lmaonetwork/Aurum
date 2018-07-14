package pro.delfik.util;

public class StringUtils {
	public static final char[] allChars = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_".toCharArray();
	
	public static boolean contains(String s, char[] c) {
		for (char a : s.toCharArray()) if (contains(c, a)) return true;
		return false;
	}
	
	public static boolean contains(char[] a, char c) {
		for (char b : a) if (b == c) return true;
		return false;
	}
	public static boolean contains(String s, char c) {
		return contains(s.toCharArray(), c);
	}
	
	public static boolean unContains(String s, char[] c) {
		for (char b : s.toCharArray()) if (!contains(c, b)) return true;
		return false;
	}
}
