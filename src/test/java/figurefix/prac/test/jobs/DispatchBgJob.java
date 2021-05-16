package figurefix.prac.test.jobs;

import figurefix.prac.jobs.DoNotCount;
import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.Alias;

@Alias("DispatchBgJob")
public class DispatchBgJob implements QuickJob, DoNotCount {

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		rt.setForward("/test/jobs/index.jsp");
		String action = rt.getRequest().getParameter("action");
		String msg = null;
		if("dispatch background job".equals(action)) {
			msg = "job uid: "+rt.issue(MyBgJob.class, "here");
		} else if("dispatch defered background job".equals(action)) {
			msg = "job uid: "+rt.issue(MyBgJob.class, "defered", 1);
		}
		rt.getRequest().setAttribute("message", msg);
	}

}
