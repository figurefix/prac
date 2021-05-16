package figurefix.prac.test.jobs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import figurefix.prac.jobs.JobService;
import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.AliasDispatcher;

/**
 * Servlet implementation class TestServlet
 */
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected final void service(HttpServletRequest req, HttpServletResponse rsp)
	        throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");	
		QuickJob job = AliasDispatcher.dispatch(this.getServletContext(), req);
		try {
			QuickJobRt jr = new QuickJobRt(req, rsp, this, 
					DsUtil.getSrvDataSource(), DsUtil.getJobDataSource());
			JobService.execute(job, jr);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
