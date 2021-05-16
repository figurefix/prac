package figurefix.prac.test.util;

import java.util.Deque;
import java.util.ArrayList;

import org.junit.Test;

import figurefix.prac.util.Parts;

public class TestParts {

	@Test
	public void testSerialText() {
		Parts p = new Parts();
		String[] sss = {"xx", "yy"};
		ArrayList<Object> arr = new ArrayList<Object>();
		arr.add(":");
		arr.add("::");
		arr.add("|");
		arr.add("||");
		ArrayList<Integer> ar = new ArrayList<Integer>();
		ar.add(1);
		ar.add(2);
		arr.add(ar);
		String one = p.join(sss, arr, "hello", 100, 12.3, true);
		System.out.println(one);
		Deque<String> list = p.part(one);
		int i = 1;
		while(list.size()>0) {
			System.out.println((i++)+": "+list.poll());
		}
	}
}
