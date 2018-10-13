package __google_.util;

import java.util.AbstractList;
import java.util.Iterator;

public class NodeList<T> extends AbstractList<T>{
	private Node node = new Node(null);

	@Override
	public boolean add(T t) {
		node.add(t);
		return true;
	}

	@Override
	public T remove(int index) {
		node.remove(index);
		return null;
	}

	@Override
	public T get(int index) {
		return node.get(index);
	}

	@Override
	public int size() {
		return node.size() - 1;
	}

	@Override
	public Iterator<T> iterator() {
		return new NodeIterator();
	}

	private class Node{
		private Node next;
		private T object;

		private Node(T object){
			this.object = object;
		}

		private T get(int index){
			if(next == null)return null;
			if(index == 0)return next.object;
			else return next.get(index - 1);
		}

		private void add(T object){
			if(next == null)next = new Node(object);
			else next.add(object);
		}

		private void remove(int index){
			if(next == null)return;
			if(index == 0)next = next.next;
			else next.remove(index - 1);
		}

		private int size(){
			return next == null ? 1 : next.size() + 1;
		}
	}

	private class NodeIterator implements Iterator<T>{
		private Node next = node;

		@Override
		public boolean hasNext() {
			return next.next != null;
		}

		@Override
		public T next() {
			return (next = next.next).object;
		}
	}
}
