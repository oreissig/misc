package struct;

import java.util.AbstractQueue;
import java.util.Deque;
import java.util.NoSuchElementException;

public abstract class AbstractDeque<E> extends AbstractQueue<E>
		implements Deque<E> {

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
