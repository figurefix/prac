package figurefix.prac.test.jobs;

import figurefix.prac.jobs.DoNotCount;
import figurefix.prac.jobs.JobService;
import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.Alias;

@Alias("CountJob")
public class CountJob implements QuickJob, DoNotCount {

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		rt.setForward("/test/jobs/index.jsp");
		String action = rt.getRequest().getParameter("action");
		String msg = null;
		if("count jobs".equals(action)) {
			msg = "job count: "+JobService.STATISTICS.JOB_COUNT.get();
		} else if("count bg jobs".equals(action)) {
			msg = "bg job count: "+JobService.STATISTICS.BG_JOB_COUNT.get();
		}
		rt.getRequest().setAttribute("message", msg);
	}

}
