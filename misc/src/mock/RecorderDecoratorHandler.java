package mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

class RecorderDecoratorHandler implements InvocationHandler {

	private final Object delegate;
	private final String name;
	private final InteractionFactory factory;

	public RecorderDecoratorHandler(Object delegate, String name,
			InteractionFactory factory) {
		this.delegate = delegate;
		this.name = name;
		this.factory = factory;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		StackTraceElement[] stack = getStack(method);
		Interaction i = factory.create(name, delegate, stack);
		try {
			Object result = method.invoke(delegate, args);
			i.setResult(result);
			return result;
		} catch (Throwable t) {
			i.setThrown(t);
			throw t;
		}
	}

	private StackTraceElement[] getStack(Method method) {
		StackTraceElement[] fullStack = new Throwable().getStackTrace();
		for (int i = 0; i < fullStack.length; i++) {
			StackTraceElement s = fullStack[i];
			if (s.getClassName().contains("Proxy")) {
				StackTraceElement[] subStack = Arrays.copyOfRange(fullStack,
						i + 1, fullStack.length);
				StackTraceElement[] invokeStack = new StackTraceElement[subStack.length + 1];
				invokeStack[0] = new StackTraceElement(delegate.getClass()
						.getName(), method.getName(), null, -1);
				System.arraycopy(subStack, 0, invokeStack, 1, subStack.length);
				return invokeStack;
			}
		}
		throw new IllegalArgumentException(
				"Stack does not contain call of delegate");
	}
}
