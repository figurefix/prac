package figurefix.prac.test.util;

import java.util.Enumeration;

import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.Alias;

@Alias("testAjaxJob")
public class TestAjaxJob implements QuickJob {

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		rt.getRequest().setCharacterEncoding("UTF-8");
		Enumeration<?> en = rt.getRequest().getParameterNames();
		StringBuffer out = new StringBuffer(rt.getRequest().getMethod()+"data:");
		while(en.hasMoreElements()) {
			String next = (String)en.nextElement();
			out.append(","+next+"="+rt.getRequest().getParameter(next));
		}
		rt.getResponse().setCharacterEncoding("UTF-8");
		rt.getResponse().getWriter().write(out.toString());		
	}

}
