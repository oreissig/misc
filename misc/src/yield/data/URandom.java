package yield.data;

import java.util.Random;

import yield.YieldingIterator;

public class URandom extends YieldingIterator<Integer> {
	
	public URandom() {
		super(100);
	}

	@Override
	protected void produce() throws InterruptedException {
		Random r = new Random();
		while (true) {
			yield(r.nextInt());
		}
	}

}
