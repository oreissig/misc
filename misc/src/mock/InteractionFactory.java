package mock;

public interface InteractionFactory {

	Interaction create(String name, Object delegate, StackTraceElement[] stack);
}
