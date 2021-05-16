package figurefix.prac.test.jobs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import figurefix.prac.jobs.JobConfig;
import figurefix.prac.jobs.JobService;
import figurefix.prac.logging.FileLogger;
import figurefix.prac.logging.SrcLog;
import figurefix.prac.test.Commons;

public class TestListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce)  {
    	SrcLog.getDefaultSrcLog().setLogger(new FileLogger(
			Commons.getTestHome()+TestListener.class.getName()+".log"
		));
    	try {
			JobConfig jfg = new JobConfig(DsUtil.getSrvDataSource(), DsUtil.getJobDataSource(), "localhost:8080");
			jfg.setWorkerThreadIdleness(10*1000);
			JobService.startup(jfg);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void contextDestroyed(ServletContextEvent sce)  { 
    	JobService.shutdown();;
    	DsUtil.destroyDS();
    }
	
}
