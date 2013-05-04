package parallelfor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This helper class is similar to {@link ParallelFor},
 * but allows for a checked exception to be thrown in
 * the step.
 * 
 * @author oreissig
 *
 * @param <T> type of elements to be iterated over
 * @param <E> type of exception that may be thrown
 */
public abstract class CheckedParallelFor<T, E extends Exception> {
	
	private static final int UNLIMITED = -1;
	private final int threads;
	
	/**
	 * Initializes a parallel for-loop executor.
	 * The default is to use as many threads as there are CPU cores available.
	 */
	public CheckedParallelFor() {
		this(false);
	}
	
	/**
	 * Initializes a parallel for-loop executor.
	 * 
	 * @param unlimited if true, then execute all steps in parallel,
	 * 		otherwise use as many threads as there are CPU cores available
	 */
	public CheckedParallelFor(boolean unlimited) {
		if (unlimited) {
			threads = UNLIMITED;
		} else {
			threads = Runtime.getRuntime().availableProcessors();
		}
	}
	
	/**
	 * Initializes a parallel for-loop executor.
	 * 
	 * @param maxThreads number of Threads to run in parallel
	 */
	public CheckedParallelFor(int maxThreads) {
		threads = maxThreads;
	}
	
	/**
	 * Performs a parallel iteration of the specified step over the given
	 * {@link Iterable}.
	 * 
	 * @param iteration to be iterated over
	 * @throws E rethrows an exception that may be thrown by a step.
	 * 		In case more than one step throws an exception, the first in the order
	 * 		of iteration is the one to be rethrown.
	 */
	public void iterate(Iterable<T> iteration) throws E {
		ExecutorService exec;
		if (threads == UNLIMITED)
			exec = Executors.newCachedThreadPool();
		else
			exec = Executors.newFixedThreadPool(threads);
		
		// hold futures that hold exceptions
		List<Future<Void>> exceptions = new ArrayList<>();
		try {
			// enqueue all elements
			for (final T i : iteration) {
				Callable<Void> call = new Callable<Void>() {
					@Override
					public Void call() throws E {
						step(i);
						return null;
					}
				};
				exceptions.add(exec.submit(call));
			}
		} finally {
			exec.shutdown();
			// check for exceptions, that may have gotten thrown
			for (Future<Void> f : exceptions) {
				try {
					f.get();
				} catch (InterruptedException ie) {
					throw new RuntimeException("exception waiting for result of iteration", ie);
				} catch (ExecutionException ee) {
					Throwable cause = ee.getCause();
					if (cause instanceof RuntimeException) {
						// cast has been checked
						throw (RuntimeException)cause;
					} else {
						// only E is allowed to be thrown
						@SuppressWarnings("unchecked")
						E e = (E)cause;
						throw e;
					}
				}
			}
		}
	}
	
	/**
	 * Implements one step of an iteration.
	 * This method contains what would usually be contained in the body of a regular for-loop.
	 * As this will be called by multiple Threads, it has to be concurrency safe.
	 * 
	 * @param item to be worked on
	 * @throws E checked exception, that may be thrown
	 */
	public abstract void step(T item) throws E;
}
