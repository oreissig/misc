package yield;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import com.google.common.collect.UnmodifiableIterator;

/**
 * @author oreissig
 * 
 * @param <T>
 *            type of iterator
 */
public abstract class YieldingIterator<T> extends UnmodifiableIterator<T> {
	private final Thread thread;
	private final BlockingQueue<T> buf;
	private final Semaphore dataReady;

	/**
	 * creates a standard {@link YieldingIterator} without read ahead
	 */
	public YieldingIterator() {
		this(1);
	}

	/**
	 * creates a {@link YieldingIterator} with the specified buffer capacity
	 * 
	 * @param readAhead
	 *            total number of items to buffer until yield(T) blocks
	 */
	public YieldingIterator(int readAhead) {
		buf = new ArrayBlockingQueue<T>(readAhead);
		dataReady = new Semaphore(0);

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					produce();
				} catch (InterruptedException e) {
					// ignore, thread ends here
				} finally {
					dataReady.release();
				}
			}
		});

		thread.start();
	}

	/**
	 * ends the produce() method
	 * 
	 * @return true, if all items have been queried.<br>
	 *         false, if there have been remaining items.
	 */
	public boolean abort() {
		if (thread.isAlive()) {
			thread.interrupt();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Method to implement the item yielding in.
	 * 
	 * @throws InterruptedException
	 *             ends the iterator prematurely
	 */
	protected abstract void produce() throws InterruptedException;

	/**
	 * Yields an item, which can then be fetched via next()
	 * 
	 * @param item
	 *            to yield
	 * @throws InterruptedException
	 *             ends the iterator prematurely
	 */
	protected void yield(T item) throws InterruptedException {
		buf.put(item);
		dataReady.release();
	}

	@Override
	public boolean hasNext() {
		dataReady.acquireUninterruptibly();
		dataReady.release(); // no data was consumed
		return !buf.isEmpty();
	}

	@Override
	public T next() {
		try {
			dataReady.acquire();
			if (buf.isEmpty()) {
				dataReady.release();
				throw new NoSuchElementException();
			} else {
				return buf.poll();
			}
		} catch (InterruptedException e) {
			return null;
		}
	}
}
