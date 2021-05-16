package figurefix.prac.test.sql;

import org.junit.Test;

import figurefix.prac.sql.Sqler;
import figurefix.prac.util.DataSet;

public class TestSqler {

	@Test
	public void test() {
		DataSet data = new DataSet();
		data.set("a.name|ge", "job");
		data.set("a.name|li", "job%");
		data.set("a.name|ni", "'a','b','c'");
		data.set("a.entry", 25);
		data.set("b.c1", 25);
		data.set("c2", 25);
		Tb1 t1 = new Tb1();
		Tb2 t2 = new Tb2();
		t1.setAlias("a");
		t2.setAlias("");
		System.out.println(Sqler.getAndClause(data, t1));
		System.out.println(Sqler.getInsertStatement(t1, data, true));
		System.out.println(Sqler.getSetClause(data, t2, t1));
		System.out.println(Sqler.getUpdateStatement(t1, data, true));
	}
}
