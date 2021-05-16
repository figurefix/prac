package figurefix.prac.test.jobs;

import figurefix.prac.jobs.QuickJobRt;

public class CustomJobRuntime {

	private QuickJobRt rt = null;
	private String tradeName = null;
	
	public CustomJobRuntime(QuickJobRt rt, String trdnam) {
		this.rt = rt;
		this.tradeName = trdnam;
	}
	
	public QuickJobRt getRuntime() {
		return rt;
	}

	public String getTradeName() {
		return tradeName;
	}
}
