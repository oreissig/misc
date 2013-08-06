package struct;

import java.util.Comparator;
import java.util.concurrent.BlockingDeque;

import com.google.common.collect.ForwardingBlockingDeque;

public class PriorityBlockingDeque<E> extends ForwardingBlockingDeque<E> {

	private PriorityBlockingDeque() {
	}

	public static <E> BlockingDeque<E> create(Comparator<? super E> comparator) {
		PriorityDeque<E> pd = PriorityDeque.create(comparator);
		return new WrapperBlockingDeque<>(pd);
	}

	public static <E> BlockingDeque<E> create(int initialCapacity,
			Comparator<? super E> comparator) {
		PriorityDeque<E> pd = PriorityDeque.create(initialCapacity, comparator);
		return new WrapperBlockingDeque<>(pd);
	}

	public static <E extends Comparable<E>> BlockingDeque<E> create() {
		PriorityDeque<E> pd = PriorityDeque.create();
		return new WrapperBlockingDeque<>(pd);
	}

	public static <E extends Comparable<E>> BlockingDeque<E> create(
			int initialCapacity) {
		PriorityDeque<E> pd = PriorityDeque.create(initialCapacity);
		return new WrapperBlockingDeque<>(pd);
	}

	@Override
	protected BlockingDeque<E> delegate() {
		throw new IllegalStateException();
	}
}
