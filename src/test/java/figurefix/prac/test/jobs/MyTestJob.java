package figurefix.prac.test.jobs;

import java.sql.DatabaseMetaData;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.Transactional;
import figurefix.prac.jobs.alias.Alias;
import figurefix.prac.logging.SrcLog;
import figurefix.prac.test.Commons;

@Alias("MyTestJob")
public class MyTestJob implements QuickJob, Transactional {
	
	static SrcLog logger = new SrcLog(
			Commons.getTestHome()+MyTestJob.class.getName()+".log");

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		rt.setForward("/test/jobs/index.jsp");
		String action = rt.getRequest().getParameter("action");
		String msg = null;
		if("connect database".equals(action)) {
			DatabaseMetaData meta = rt.getDBA().getConnection().getMetaData();
			msg = "JDBC driver version is " + meta.getDriverVersion();
		} else if("make sql error".equals(action)) {
			rt.getDBA().getConnection().createStatement()
				.executeUpdate("update ttt set tt='a' where t='a'");
		} else if("write file log".equals(action)) {
			logger.logTrace("log test "+System.currentTimeMillis());
		}
		rt.getRequest().setAttribute("message", msg);
	}

}
