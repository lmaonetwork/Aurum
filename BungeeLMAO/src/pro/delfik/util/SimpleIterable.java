package pro.delfik.util;


import java.util.Iterator;

public class SimpleIterable<T> implements java.lang.Iterable<T> {
	private final Iterator<T> iterator;
	
	public SimpleIterable(Iterator<T> iterator) {
		this.iterator = iterator;
	}
	
	public Iterator<T> iterator() {
		return this.iterator != null ? this.iterator : new Iterator<T>() {
			public boolean hasNext() {
				return false;
			}
			
			public T next() {
				return null;
			}
		};
	}
}

