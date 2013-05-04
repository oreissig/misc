package parallelfor;

import java.util.Iterator;

import com.google.common.collect.UnmodifiableIterator;

public class IntRange implements Iterable<Integer> {

	private final int start;
	private final int end;
	private final int step;

	public IntRange(int start, int end) {
		this(start, end, 1);
	}

	public IntRange(int start, int end, int step) {
		this.start = start;
		this.end = end;
		this.step = step;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new UnmodifiableIterator<Integer>() {
			private int current = start;

			@Override
			public boolean hasNext() {
				return current < end;
			}

			@Override
			public Integer next() {
				int next = current;
				current += step;
				return next;
			}
		};
	}
}
