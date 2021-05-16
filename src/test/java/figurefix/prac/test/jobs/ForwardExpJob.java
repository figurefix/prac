package figurefix.prac.test.jobs;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.Alias;

@Alias("ForwardExpJob")
public class ForwardExpJob implements QuickJob {

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		rt.setForward("/test/jobs/exp.jsp");
		throw new Exception("forward exception");
	}

}
