package figurefix.prac.test.jobs;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;

public abstract class CustomJob implements QuickJob {

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		CustomJobRuntime crt = new CustomJobRuntime(rt, "custom");
		this.exe(crt);
	}
	
	protected abstract void exe(CustomJobRuntime crt) throws Exception;

}
