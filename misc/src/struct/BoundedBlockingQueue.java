package struct;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * This decorator takes an arbitrary {@link BlockingQueue} and makes it bounded.
 * It is useful for implementations, that do not provide a bounded variant, such
 * as {@link PriorityBlockingQueue}.
 * <p>
 * The implementation synchronizes on the given queue object and checks the
 * queue's size before inserting a new element.
 * 
 * @author oreissig
 */
public class BoundedBlockingQueue<E> implements BlockingQueue<E> {

	private final BlockingQueue<E> q;
	private final int capacity;

	/**
	 * Creates a new bounded decorator for the given BlockingQueue instance.
	 * 
	 * @param delegate
	 *            to decorate
	 * @param capacity
	 *            upper bound of elements
	 * @throws IllegalArgumentException
	 *             if <tt>delegate</tt> already contains more than
	 *             <tt>capacity</tt> elements.
	 */
	public BoundedBlockingQueue(BlockingQueue<E> delegate, int capacity) {
		if (delegate.size() > capacity)
			throw new IllegalArgumentException(
					"Given queue already contains more elements than allowed");

		this.q = delegate;
		this.capacity = capacity;
	}

	/*
	 * altered methods
	 */

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c)
			modified |= add(e);
		return modified;
	}

	@Override
	public boolean add(E e) {
		synchronized (q) {
			if (q.size() >= capacity)
				throw new IllegalStateException("no capacity left");
			return q.add(e);
		}
	}

	@Override
	public boolean offer(E e) {
		synchronized (q) {
			while (q.size() >= capacity)
				try {
					wait();
				} catch (InterruptedException e1) {
					return false;
				}
			return q.offer(e);
		}
	}

	@Override
	public void put(E e) throws InterruptedException {
		synchronized (q) {
			while (q.size() >= capacity)
				wait();
			q.put(e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link #wait(long)} is insufficient, because it cannot be determined if
	 * the timeout was reached or the method returned because it has been
	 * {@link #notify()}ed.
	 * 
	 * Use a solution based on {@link Future}s instead.
	 * 
	 * @see <a
	 *      href=http://stackoverflow.com/questions/2275443/how-to-timeout-a-thread
	 *      /2275596#2275596>StackOverflow</a>
	 */
	@Override
	public boolean offer(final E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<?> f = exec.submit(new Runnable() {
			@Override
			public void run() {
				synchronized (q) {
					while (q.size() >= capacity)
						try {
							wait();
						} catch (InterruptedException e1) {
							// try again
						}
					q.offer(e);
				}
			}
		});
		try {
			f.get(timeout, unit);
			return true;
		} catch (TimeoutException | ExecutionException e1) {
			return !f.cancel(true);
		}
	}

	@Override
	public boolean remove(Object o) {
		synchronized (q) {
			boolean b = q.remove(o);
			notify();
			return b;
		}
	}

	@Override
	public E remove() {
		synchronized (q) {
			E e = q.remove();
			notify();
			return e;
		}
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		synchronized (q) {
			boolean b = q.removeAll(c);
			notifyAll();
			return b;
		}
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		synchronized (q) {
			boolean b = q.retainAll(c);
			notifyAll();
			return b;
		}
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		synchronized (q) {
			int i = q.drainTo(c);
			if (i > 1)
				notifyAll();
			else if (i == 1)
				notify();
			return i;
		}
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		synchronized (q) {
			int i = q.drainTo(c, maxElements);
			if (i > 1)
				notifyAll();
			else if (i == 1)
				notify();
			return i;
		}
	}

	@Override
	public void clear() {
		synchronized (q) {
			q.clear();
			notifyAll();
		}
	}

	@Override
	public E poll() {
		synchronized (q) {
			E e = q.poll();
			if (e != null)
				notify();
			return e;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * {@link #wait(long)} is insufficient, because it cannot be determined if
	 * the timeout was reached or the method returned because it has been
	 * {@link #notify()}ed.
	 * 
	 * Use a solution based on {@link Future}s instead.
	 * 
	 * @see <a
	 *      href=http://stackoverflow.com/questions/2275443/how-to-timeout-a-thread
	 *      /2275596#2275596>StackOverflow</a>
	 */
	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Future<E> f = exec.submit(new Callable<E>() {
			@Override
			public E call() {
				synchronized (q) {
					while (q.size() >= capacity)
						try {
							wait();
						} catch (InterruptedException e1) {
							// try again
						}
					return q.poll();
				}
			}
		});
		try {
			return f.get(timeout, unit);
		} catch (TimeoutException | ExecutionException e1) {
			f.cancel(true);
			return null;
		}
	}

	@Override
	public E take() throws InterruptedException {
		synchronized (q) {
			E e = q.take();
			notify();
			return e;
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private final Iterator<E> i = q.iterator();

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
				synchronized (q) {
					i.remove();
					notify();
				}
			}
		};
	}

	@Override
	public int remainingCapacity() {
		return capacity - q.size();
	}

	/*
	 * just plain delegates
	 */

	@Override
	public E element() {
		return q.element();
	}

	@Override
	public E peek() {
		return q.peek();
	}

	@Override
	public int size() {
		return q.size();
	}

	@Override
	public boolean isEmpty() {
		return q.isEmpty();
	}

	@Override
	public Object[] toArray() {
		return q.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return q.toArray(a);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return q.containsAll(c);
	}

	@Override
	public boolean contains(Object o) {
		return q.contains(o);
	}
}
