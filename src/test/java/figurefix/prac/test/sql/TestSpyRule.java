package figurefix.prac.test.sql;

import figurefix.prac.sql.SpyRule;
import figurefix.prac.sql.SqlInfo;

public class TestSpyRule implements SpyRule {

	@Override
	public boolean match(SqlInfo info) {
		return true;
	}

	@Override
	public boolean match(String classname, String method, Object[] args) {
		return true;
	}

}
