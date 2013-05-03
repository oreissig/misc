package yield.data;

import yield.YieldingIterator;


public class TestData extends YieldingIterator<String> {
	
	@Override
	protected void produce() throws InterruptedException {
		yield("123");
		yield("456");
		
		for (Integer i=0; i<5; i++)
			yield(i.toString());
	}
}
