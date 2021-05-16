package figurefix.prac.test.sql;

import figurefix.prac.sql.Column;
import figurefix.prac.sql.Table;

public class Tb1 extends Table {

	public final Column name = addPK("name", Column.QUOTED);
	public final Column entry = addColumn("entry", Column.NUMBER);
	public final Column remark = addColumn("remark", Column.QUOTED);
	
	public Tb1() {
		super("table1");
	}
}
