package mock;

class PrintingInteraction implements Interaction {
	public static int DELAY = 100;
	private final Interaction delegate;
	private static StackTraceElement[] previous;

	static {
		previous = new StackTraceElement[0];
	}

	public PrintingInteraction(Interaction delegate) {
		this.delegate = delegate;

		prefix();

		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {
			// fuck it
		}
	}

	private void prefix() {
		StackTraceElement[] stack = delegate.getStack();
		for (int i=stack.length-1; i>=0; i--) {
			StackTraceElement s = stack[i];
			if (previous.length <= i || !s.equals(previous[i]))
				indent(stack.length - i - 1);
				System.out.println(s.toString());
		}
		previous = stack;
	}

	private static void indent(int steps) {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<steps; i++)
			sb.append(' ');
		System.out.print(sb.toString());
	}

	@Override
	public void setThrown(Throwable thrown) {
		indent(delegate.getStack().length - 1);
		System.out.println("-> throwing " + thrown.getMessage());
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {
			// fuck it
		}
		delegate.setThrown(thrown);
	}

	@Override
	public void setResult(Object result) {
		indent(delegate.getStack().length - 1);
		System.out.println("-> result " + result);
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {
			// fuck it
		}
		delegate.setResult(result);
	}

	// just plain delegates below

	@Override
	public String getName() {
		return delegate.getName();
	}

	@Override
	public Object getDelegate() {
		return delegate.getDelegate();
	}

	@Override
	public StackTraceElement[] getStack() {
		return delegate.getStack();
	}

	@Override
	public Throwable getThrown() {
		return delegate.getThrown();
	}

	@Override
	public Object getResult() {
		return delegate.getResult();
	}
}