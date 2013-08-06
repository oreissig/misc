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

public class BoundedBlockingDeque<E> extends AbstractQueue<E> implements
		BlockingDeque<E> {

	private final Semaphore avail = new Semaphore(0);
	private final Semaphore free;
	private final Deque<E> d;

	public BoundedBlockingDeque(Deque<E> delegate) {
		this.d = delegate;
		free = new Semaphore(Integer.MAX_VALUE);
	}

	public BoundedBlockingDeque(Deque<E> delegate, int capacity) {
		this.d = delegate;
		free = new Semaphore(capacity);
	}

	@Override
	public void addFirst(E e) {
		if (!free.tryAcquire())
			throw new IllegalStateException("full");
		d.addFirst(e);
		avail.release();
	}

	@Override
	public void addLast(E e) {
		if (!free.tryAcquire())
			throw new IllegalStateException("full");
		d.addLast(e);
		avail.release();
	}

	@Override
	public boolean offerFirst(E e) {
		if (!free.tryAcquire())
			return false;
		boolean success = d.offerFirst(e);
		if (success)
			avail.release();
		return success;
	}

	@Override
	public boolean offerLast(E e) {
		if (!free.tryAcquire())
			return false;
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
		if (!free.tryAcquire(timeout, unit))
			return false;
		boolean success = d.offerFirst(e);
		if (success)
			avail.release();
		return success;
	}

	@Override
	public boolean offerLast(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if (!free.tryAcquire(timeout, unit))
			return false;
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
		free.release(s);
		return s;
	}

	@Override
	public int drainTo(Collection<? super E> c, final int maxElements) {
		// hack: final pointer to mutable integer
		final int[] n = { 0 };
		final BlockingDeque<E> me = this;

		Collection<E> drain = new AbstractCollection<E>() {
			@Override
			public Iterator<E> iterator() {
				return new UnmodifiableIterator<E>() {
					private final Iterator<E> it = me.iterator();

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
		free.release();
		return e;
	}

	@Override
	public E removeLast() {
		if (!avail.tryAcquire())
			throw new NoSuchElementException();
		E e = d.removeLast();
		free.release();
		return e;
	}

	@Override
	public E pollFirst() {
		if (!avail.tryAcquire())
			return null;
		E e = d.pollFirst();
		free.release();
		return e;
	}

	@Override
	public E pollLast() {
		if (!avail.tryAcquire())
			return null;
		E e = d.pollLast();
		free.release();
		return e;
	}

	@Override
	public E poll() {
		return pollFirst();
	}

	@Override
	public E pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
		if (avail.tryAcquire(timeout, unit)) {
			E e = d.pollFirst();
			if (e != null)
				free.release();
			return e;
		} else
			return null;
	}

	@Override
	public E pollLast(long timeout, TimeUnit unit) throws InterruptedException {
		if (avail.tryAcquire(timeout, unit)) {
			E e = d.pollLast();
			if (e != null)
				free.release();
			return e;
		} else
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
		E e = d.peekFirst();
		free.release();
		return e;
	}

	@Override
	public E getLast() {
		if (!avail.tryAcquire())
			throw new NoSuchElementException();
		E e = d.peekLast();
		free.release();
		return e;
	}

	@Override
	public E peekFirst() {
		if (!avail.tryAcquire())
			return null;
		E e = d.peekFirst();
		free.release();
		return e;
	}

	@Override
	public E peekLast() {
		if (!avail.tryAcquire())
			return null;
		E e = d.peekLast();
		free.release();
		return e;
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
		free.acquire();
		d.addFirst(e);
		avail.release();
	}

	@Override
	public void putLast(E e) throws InterruptedException {
		free.acquire();
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
		E e = d.removeFirst();
		free.release();
		return e;
	}

	@Override
	public E takeLast() throws InterruptedException {
		avail.acquire();
		E e = d.removeLast();
		free.release();
		return e;
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
		if (changed) {
			free.release();
		} else {
			avail.release();
		}
		return changed;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		if (!avail.tryAcquire())
			return false;
		boolean changed = d.removeLastOccurrence(o);
		if (changed) {
			free.release();
		} else {
			avail.release();
		}
		return changed;
	}

	@Override
	public Iterator<E> iterator() {
		return new WrappedIterator(d.iterator());
	}

	@Override
	public Iterator<E> descendingIterator() {
		return new WrappedIterator(d.descendingIterator());
	}

	@Override
	public int size() {
		return avail.availablePermits();
	}

	private class WrappedIterator implements Iterator<E> {

		private final Iterator<E> i;

		public WrappedIterator(Iterator<E> wrapped) {
			i = wrapped;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public E next() {
			return i.next();
		}

		@Override
		public void remove() {
			if (avail.tryAcquire())
				try {
					i.remove();
					free.release();
				} catch (Exception e) {
					avail.release();
				}
			else
				throw new IllegalStateException();
		}
	}
}
