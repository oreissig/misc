package mock;

public class SimpleInteractionFactory implements InteractionFactory {

	@Override
	public Interaction create(String name, Object delegate,
			StackTraceElement[] stack) {
		return new BeanInteractionImpl(name, delegate, stack);
	}
}

class BeanInteractionImpl implements Interaction {
	private final String name;
	private final Object delegate;
	private final StackTraceElement[] stack;
	private Object result;
	private Throwable thrown;

	public BeanInteractionImpl(String name, Object delegate,
			StackTraceElement[] stack) {
		this.name = name;
		this.delegate = delegate;
		this.stack = stack;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Object getDelegate() {
		return delegate;
	}

	@Override
	public StackTraceElement[] getStack() {
		return stack;
	}

	@Override
	public Throwable getThrown() {
		return thrown;
	}

	@Override
	public void setThrown(Throwable thrown) {
		this.thrown = thrown;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public void setResult(Object result) {
		this.result = result;
	}
}
