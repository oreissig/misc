package struct;

import java.util.AbstractQueue;
import java.util.Iterator;

public abstract class AbstractQueue2<E> extends AbstractQueue<E> {

	@Override
	public E poll() {
		Iterator<E> i = iterator();
		if (i.hasNext()) {
			E e = i.next();
			i.remove();
			return e;
		} else {
			return null;
		}
	}

	@Override
	public E peek() {
		Iterator<E> i = iterator();
		if (i.hasNext()) {
			return i.next();
		} else {
			return null;
		}
	}
}
