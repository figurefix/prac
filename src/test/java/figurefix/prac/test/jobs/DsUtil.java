package figurefix.prac.test.jobs;

import java.util.HashMap;
import java.util.Set;

import javax.sql.DataSource;

import figurefix.prac.logging.SrcLog;

public class DsUtil {

	private static HashMap<String, DsWrap> map = new HashMap<String, DsWrap>();

	public static synchronized DataSource getDataSource(
			String dsname, String driver, String url, String usr, String pwd) 
			throws Exception {
		if( ! map.containsKey(dsname)) {
			try {
				DsWrap dsw = new C3p0DsWrap(dsname, driver, url, usr, pwd);
				map.put(dsname, dsw);
			} catch (Exception e) {
				SrcLog.error(e);
			}
		}
		return map.get(dsname).getDS();
	}
	
	public static synchronized DataSource getJobDataSource() throws Exception {
		return getDataSource(
				"jobds", 
				"org.gjt.mm.mysql.Driver", 
				"jdbc:mysql://127.0.0.1:3306/test", 
				"root", 
				"");
	}
	
	public static synchronized DataSource getSrvDataSource() throws Exception {
		return getDataSource(
				"srvds", 
				"org.apache.derby.jdbc.ClientDriver", 
				"jdbc:derby://127.0.0.1:1527/prac;create=true", 
				"app", 
				"app");
	}
	
	public static synchronized void destroyDS() {
		try {
			Set<String> nset = map.keySet();
			for(String name : nset) {
				map.get(name).destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
