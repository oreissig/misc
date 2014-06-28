package mock;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;

public class RecorderFactory implements AutoCloseable {
	private final InteractionFactory factory;
	final List<Interaction> interactions = synchronizedList(new ArrayList<>());

	public RecorderFactory(InteractionFactory factory) {
		this.factory = new InteractionFactory() {
			@Override
			public Interaction create(String name, Object delegate,
					StackTraceElement[] stack) {
				Interaction i = factory.create(name, delegate, stack);
				interactions.add(i);
				return i;
			}
		};
	}

	@SuppressWarnings("unchecked")
	public <T> T record(T object, Class<?>... ifaces) {
		return (T) newProxyInstance(getClass().getClassLoader(), ifaces,
				new RecorderDecoratorHandler(object, object.toString(), factory));
	}

	@SuppressWarnings("unchecked")
	public <T> T record(T object, String alias, Class<?>... ifaces) {
		return (T) newProxyInstance(getClass().getClassLoader(), ifaces,
				new RecorderDecoratorHandler(object, alias, factory));
	}

	@Override
	public void close() {
		System.out.println(interactions.size()
				+ " interactions have been recorded");
		/*for (Interaction i : interactions) {
			System.out.println(i.getName()
					+ " has been called "
					+ i.getStack()[0]
					+ ((i.getThrown() == null) ? " with result "
							+ i.getResult() : " throwing "
							+ i.getThrown().getMessage()));
		}*/
	}
}
