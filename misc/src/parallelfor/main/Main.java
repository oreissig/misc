package parallelfor.main;

import parallelfor.IntRange;
import parallelfor.ParallelFor;

public class Main {
	public static void main(String[] args) {
		Iterable<Integer> data = new IntRange(0, 100, 2);
		ParallelFor<Integer> parFor = new ParallelFor<Integer>() {
			@Override
			public void step(Integer item) {
				System.out.println(item);
			}
		};
		parFor.iterate(data);
	}
}
