package figurefix.prac.test.util;

import org.junit.Test;

import figurefix.prac.util.DateTime;

public class TestDateTime {

	@Test
	public void testNew1() throws Exception {
		DateTime d = new DateTime("2018/1/1");
		System.out.println(d.toString());
	}
	
	@Test
	public void testNew2() throws Exception {
		DateTime d = new DateTime("8/1/1 123401 999888");
		System.out.println(d.toString());
	}

	@Test
	public void testEarlier() throws Exception {
		DateTime dt = DateTime.getEarliest(
				new DateTime("2018-01-01 12:32:01 1"), 
				new DateTime("2018-01-01 12:32:01 10"),
				null);
		System.out.println(dt);
	}
}
