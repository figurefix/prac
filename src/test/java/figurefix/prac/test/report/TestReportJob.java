package figurefix.prac.test.report;

import java.io.File;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.Alias;
import figurefix.prac.report.Report;

@Alias("testReportJob")
public class TestReportJob implements QuickJob {

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		Report rpt = Report.getInstance(rt.getServletContext().getRealPath("/")
				+ "test/report/sample.xlsx".replace('/', File.separatorChar));
		rpt.export(TestReport.buildDataSet(), rt.getResponse(), "export.xlsx");
	}

}
