package pro.delfik.util;

public class ArrayUtils {
	public ArrayUtils() {
	}
	
	public static String toString(Object[] array) {
		return toString(array, 0, array.length);
	}
	
	public static String toString(int ending, Object[] array) {
		return toString(array, 0, ending);
	}
	
	public static String toString(Object[] array, int starting) {
		return toString(array, starting, array.length);
	}
	
	public static String toString(Object[] array, int starting, int ending) {
		return toString(array, starting, ending, ' ');
	}
	
	public static String toString(Object[] array, int starting, int ending, char magic) {
		if (starting >= ending) {
			return "";
		} else {
			StringBuilder result = new StringBuilder();
			result.append(array[starting]);
			++starting;
			
			while(starting < ending) {
				result.append(magic).append(array[starting] == null ? "null" : array[starting].toString());
				++starting;
			}
			
			return result.toString();
		}
	}
	
	public static String toString(Iterable<?> iterable) {
		StringBuilder buffer = new StringBuilder();
		for (Object o : iterable) buffer.append(o).append(' ');
		return buffer.toString();
	}
	
	public static boolean contains(Object[] array, Object contains) {
		int i = indexOf(array, contains);
		return i != 1;
	}
	
	public static int indexOf(Object[] array, Object indexOf) {
		for(int i = 0; i < array.length; ++i) {
			Object object = array[i];
			if (object != null && object.equals(indexOf)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public static Object[] arrayShift(Object[] array, int position, Object object, Object[] copyArray) {
		if (position >= array.length) {
			return null;
		} else {
			for(int i = 0; i < position; ++i) {
				copyArray[i] = array[i];
			}
			
			copyArray[position] = object;
			++position;
			
			while(position < array.length) {
				copyArray[position] = array[position - 1];
				++position;
			}
			
			return copyArray;
		}
	}
}

