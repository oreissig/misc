package mock;

public class PrintingInteractionFactory implements InteractionFactory {

	private final InteractionFactory factory;

	public PrintingInteractionFactory(InteractionFactory factory) {
		this.factory = factory;
	}

	@Override
	public Interaction create(String name, Object delegate,
			StackTraceElement[] stack) {
		Interaction i = factory.create(name, delegate, stack);
		return new PrintingInteraction(i);
	}
}
