package figurefix.prac.test.jobs;

import java.io.File;

import figurefix.prac.jobs.Job;
import figurefix.prac.jobs.JobRuntime;
import figurefix.prac.test.Commons;

public class MyBgJob implements Job {

	@Override
	public void execute(JobRuntime rt) throws Exception {
		try {
			Thread.sleep(5000);
		} catch (Exception e) {

		}
		File f = new File(Commons.getTestHome()+"a."+rt.getParam()+".job."+System.currentTimeMillis());
		if(!f.exists()) {
			f.createNewFile();
		}
	}

}
