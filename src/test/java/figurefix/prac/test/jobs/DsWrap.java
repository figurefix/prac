package figurefix.prac.test.jobs;

import javax.sql.DataSource;

public interface DsWrap {

	public DataSource getDS() throws Exception;
	
	public void destroy() throws Exception;
}
