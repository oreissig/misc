package struct;

import java.util.AbstractQueue;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AbstractDeque<E> extends AbstractQueue<E>
		implements Deque<E> {

	@Override
	public E pollFirst() {
		return poll(iterator());
	}

	@Override
	public E pollLast() {
		return poll(descendingIterator());
	}

	private E poll(Iterator<E> i) {
		if (i.hasNext()) {
			E e = i.next();
			i.remove();
			return e;
		} else {
			return null;
		}
	}

	@Override
	public E peekFirst() {
		return peek(iterator());
	}

	@Override
	public E peekLast() {
		return peek(descendingIterator());
	}

	private E peek(Iterator<E> i) {
		if (i.hasNext()) {
			return i.next();
		} else {
			return null;
		}
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		return remove(o, iterator());
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		return remove(o, descendingIterator());
	}

	private boolean remove(Object o, Iterator<E> i) {
		while (i.hasNext()) {
			E e = i.next();
			if (o == null ? e == null : o.equals(e)) {
				i.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public void addFirst(E e) {
		if (!offerFirst(e))
			throw new IllegalStateException("Queue full");
	}

	@Override
	public void addLast(E e) {
		if (!offerLast(e))
			throw new IllegalStateException("Queue full");
	}

	@Override
	public E removeFirst() {
		E x = pollFirst();
		if (x != null)
			return x;
		else
			throw new NoSuchElementException();
	}

	@Override
	public E removeLast() {
		E x = pollLast();
		if (x != null)
			return x;
		else
			throw new NoSuchElementException();
	}

	@Override
	public E getFirst() {
		E x = peekFirst();
		if (x != null)
			return x;
		else
			throw new NoSuchElementException();
	}

	@Override
	public E getLast() {
		E x = peekLast();
		if (x != null)
			return x;
		else
			throw new NoSuchElementException();
	}

	@Override
	public boolean offer(E e) {
		return offerLast(e);
	}

	@Override
	public E poll() {
		return pollFirst();
	}

	@Override
	public E peek() {
		return peekFirst();
	}

	@Override
	public void push(E e) {
		addFirst(e);
	}

	@Override
	public E pop() {
		return removeFirst();
	}
}
