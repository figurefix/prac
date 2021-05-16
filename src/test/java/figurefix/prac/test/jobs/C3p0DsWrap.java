package figurefix.prac.test.jobs;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

import figurefix.prac.logging.FileLogger;
import figurefix.prac.logging.SrcLog;
import figurefix.prac.sql.SqlSpy;
import figurefix.prac.test.Commons;

public class C3p0DsWrap implements DsWrap {

	private DataSource ds = null;
	
	C3p0DsWrap(String name, String driver, String url, String usr, String pwd) throws Exception {
		Class.forName(driver);
		this.ds = DataSources.unpooledDataSource(url, usr, pwd);
		Map<String, Object> overrides = new HashMap<String, Object>();
		overrides.put("maxPoolSize", new Integer(5)); //"boxed primitives" also work
		this.ds = DataSources.pooledDataSource(this.ds, overrides);
		this.ds.setLoginTimeout(1);
		SrcLog slog = new SrcLog(new FileLogger(
				Commons.getTestHome()+name+".sql.log"));
		slog.setTracing(true);
		this.ds = SqlSpy.tap(this.ds, name, slog);
	}
	
	@Override
	public DataSource getDS() throws Exception {
		return this.ds;
	}

	@Override
	public void destroy() throws Exception {
		DataSources.destroy(ds);
		this.ds = null;
	}
}
