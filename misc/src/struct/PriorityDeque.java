package struct;

import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import com.google.common.collect.ForwardingQueue;
import com.google.common.collect.MinMaxPriorityQueue;

public class PriorityDeque<E> extends ForwardingQueue<E> implements Deque<E> {

	private final MinMaxPriorityQueue<E> q;

	private PriorityDeque(Comparator<? super E> comparator) {
		q = MinMaxPriorityQueue.orderedBy(comparator).create();
	}

	private PriorityDeque(int initialCapacity, Comparator<? super E> comparator) {
		q = MinMaxPriorityQueue.orderedBy(comparator)
				.expectedSize(initialCapacity).create();
	}

	public static <E> PriorityDeque<E> create(Comparator<? super E> comparator) {
		return new PriorityDeque<>(comparator);
	}

	public static <E> PriorityDeque<E> create(int initialCapacity,
			Comparator<? super E> comparator) {
		return new PriorityDeque<>(comparator);
	}

	public static <E extends Comparable<E>> PriorityDeque<E> create() {
		Comparator<? super E> comparator = new ComparableComparator<>();
		return new PriorityDeque<>(comparator);
	}

	public static <E extends Comparable<E>> PriorityDeque<E> create(
			int initialCapacity) {
		Comparator<? super E> comparator = new ComparableComparator<>();
		return new PriorityDeque<>(initialCapacity, comparator);
	}

	private static class ComparableComparator<E extends Comparable<E>>
			implements Comparator<E> {
		@Override
		public int compare(E o1, E o2) {
			return o1.compareTo(o2);
		}
	}

	@Override
	protected Queue<E> delegate() {
		return q;
	}

	@Deprecated
	@Override
	public void addFirst(E e) {
		q.add(e);
	}

	@Deprecated
	@Override
	public void addLast(E e) {
		q.add(e);
	}

	@Deprecated
	@Override
	public boolean offerFirst(E e) {
		return q.offer(e);
	}

	@Deprecated
	@Override
	public boolean offerLast(E e) {
		return q.offer(e);
	}

	@Override
	public E removeFirst() {
		return q.removeFirst();
	}

	@Override
	public E removeLast() {
		return q.removeLast();
	}

	@Override
	public E pollFirst() {
		return q.pollFirst();
	}

	@Override
	public E pollLast() {
		return q.pollLast();
	}

	@Override
	public E getFirst() {
		E e = peekFirst();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public E getLast() {
		E e = peekLast();
		if (e == null)
			throw new NoSuchElementException();
		return e;
	}

	@Override
	public E peekFirst() {
		return q.peekFirst();
	}

	@Override
	public E peekLast() {
		return q.peekLast();
	}

	@Deprecated
	@Override
	public boolean removeFirstOccurrence(Object o) {
		return q.remove(o);
	}

	@Deprecated
	@Override
	public boolean removeLastOccurrence(Object o) {
		return q.remove(o);
	}

	@Deprecated
	@Override
	public void push(E e) {
		q.add(e);
	}

	@Deprecated
	@Override
	public E pop() {
		return q.removeFirst();
	}

	@Deprecated
	@Override
	public Iterator<E> descendingIterator() {
		return q.iterator();
	}
}
