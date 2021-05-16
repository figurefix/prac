package figurefix.prac.test.sql;

import figurefix.prac.sql.Column;
import figurefix.prac.sql.Table;

public class Tb2 extends Table {

	public final Column c1 = addPK("c1", Column.QUOTED);
	public final Column c2 = addColumn("c2", Column.NUMBER);
	
	public Tb2() {
		super("table2");
	}
}
