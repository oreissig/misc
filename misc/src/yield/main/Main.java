package yield.main;
import yield.YieldingIterator;
import yield.data.TestData;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		YieldingIterator<String> test = new TestData();
		while (test.hasNext())
			System.out.println(test.next());
		
		System.out.println("cleanly? " + test.abort());
	}

}
