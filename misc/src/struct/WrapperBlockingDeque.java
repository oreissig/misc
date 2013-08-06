package struct;

import java.util.AbstractCollection;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.UnmodifiableIterator;

public class WrapperBlockingDeque<E> extends AbstractQueue<E> implements
		BlockingDeque<E> {

	private final Semaphore avail = new Semaphore(0);
	private final Deque<E> d;

	public WrapperBlockingDeque(Deque<E> delegate) {
		this.d = delegate;
	}

	@Override
	public void addFirst(E e) {
		d.addFirst(e);
		avail.release();
	}

	@Override
	public void addLast(E e) {
		d.addLast(e);
		avail.release();
	}

	@Override
	public boolean offerFirst(E e) {
		boolean success = d.offerFirst(e);
		if (success)
			avail.release();
		return success;
	}

	@Override
	public boolean offerLast(E e) {
		boolean success = d.offerLast(e);
		if (success)
			avail.release();
		return success;
	}

	@Override
	public boolean offer(E e) {
		return offerLast(e);
	}

	@Override
	public boolean offerFirst(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		boolean success = d.offerFirst(e);
		if (success)
			avail.release();
		return success;
	}

	@Override
	public boolean offerLast(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		boolean success = d.offerLast(e);
		if (success)
			avail.release();
		return success;
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		return offerLast(e, timeout, unit);
	}

	@Override
	public int remainingCapacity() {
		return avail.availablePermits();
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		int s = avail.drainPermits();
		c.addAll(d);
		d.clear();
		return s;
	}

	@Override
	public int drainTo(Collection<? super E> c, final int maxElements) {
		// hack: final pointer to mutable integer
		final int[] n = { 0 };

		Collection<E> drain = new AbstractCollection<E>() {
			@Override
			public Iterator<E> iterator() {
				return new UnmodifiableIterator<E>() {
					private final Iterator<E> it = d.iterator();

					@Override
					public boolean hasNext() {
						return n[0] < maxElements && it.hasNext();
					}

					@Override
					public E next() {
						E e = it.next();
						it.remove();
						n[0]++;
						return e;
					}
				};
			}

			@Override
			public int size() {
				return maxElements;
			}
		};

		c.addAll(drain);

		return n[0];
	}

	@Override
	public E removeFirst() {
		if (!avail.tryAcquire())
			throw new NoSuchElementException();
		E e = d.removeFirst();
		return e;
	}

	@Override
	public E removeLast() {
		if (!avail.tryAcquire())
			throw new NoSuchElementException();
		return d.removeLast();
	}

	@Override
	public E pollFirst() {
		if (!avail.tryAcquire())
			return null;
		return d.pollFirst();
	}

	@Override
	public E pollLast() {
		if (!avail.tryAcquire())
			return null;
		return d.pollLast();
	}

	@Override
	public E poll() {
		return pollFirst();
	}

	@Override
	public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
		if (avail.tryAcquire(timeout, unit))
			return d.pollFirst();
		else
			return null;
	}

	@Override
	public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
		if (avail.tryAcquire(timeout, unit))
			return d.pollLast();
		else
			return null;
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		return pollFirst(timeout, unit);
	}

	@Override
	public E getFirst() {
		if (!avail.tryAcquire())
			throw new NoSuchElementException();
		return d.peekFirst();
	}

	@Override
	public E getLast() {
		if (!avail.tryAcquire())
			throw new NoSuchElementException();
		return d.peekLast();
	}

	@Override
	public E peekFirst() {
		if (!avail.tryAcquire())
			return null;
		return d.peekFirst();
	}

	@Override
	public E peekLast() {
		if (!avail.tryAcquire())
			return null;
		return d.peekLast();
	}

	@Override
	public E peek() {
		return peekFirst();
	}

	@Override
	public E pop() {
		return removeFirst();
	}

	@Override
	public void push(E e) {
		addFirst(e);
	}

	@Override
	public void putFirst(E e) throws InterruptedException {
		d.addFirst(e);
		avail.release();
	}

	@Override
	public void putLast(E e) throws InterruptedException {
		d.addLast(e);
		avail.release();
	}

	@Override
	public void put(E e) throws InterruptedException {
		putLast(e);
	}

	@Override
	public E takeFirst() throws InterruptedException {
		avail.acquire();
		return d.removeFirst();
	}

	@Override
	public E takeLast() throws InterruptedException {
		avail.acquire();
		return d.removeLast();
	}

	@Override
	public E take() throws InterruptedException {
		return takeFirst();
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		if (!avail.tryAcquire())
			return false;
		boolean changed = d.removeFirstOccurrence(o);
		if (!changed)
			avail.release();
		return changed;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		if (!avail.tryAcquire())
			return false;
		boolean changed = d.removeLastOccurrence(o);
		if (changed)
			avail.release();
		return changed;
	}

	@Override
	public Iterator<E> iterator() {
		return d.iterator();
	}

	@Override
	public Iterator<E> descendingIterator() {
		return d.descendingIterator();
	}

	@Override
	public int size() {
		return avail.availablePermits();
	}
}
