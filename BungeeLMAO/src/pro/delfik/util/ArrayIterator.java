package pro.delfik.util;

public class ArrayIterator<T> implements java.util.Iterator {
	
	public ArrayIterator(T[] array) {
		this.array = array;
	}
	
	private final T[] array;
	private int position = 0;
	
	@Override
	public boolean hasNext() {
		return array.length > position;
	}
	
	@Override
	public T next() {
		return array[position++];
	}
}
