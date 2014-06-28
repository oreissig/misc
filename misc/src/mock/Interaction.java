package mock;

public interface Interaction {
	String getName();
	Object getDelegate();
	StackTraceElement[] getStack();

	Throwable getThrown();
	void setThrown(Throwable thrown);

	Object getResult();
	void setResult(Object result);
}
