package figurefix.prac.test.httpmsg;

import figurefix.prac.httpmsg.HttpDispatcher;
import figurefix.prac.jobs.QuickJob;
import figurefix.prac.jobs.QuickJobRt;
import figurefix.prac.jobs.alias.Alias;

@Alias("testHttpMessageJob")
public class TestHttpMessageJob implements QuickJob {

	@Override
	public void execute(QuickJobRt rt) throws Exception {
		HttpDispatcher svr = new HttpDispatcher(rt.getRequest(), rt.getResponse());
//		byte[] bu = svr.listenBytes();
//		svr.reply(bu);
		System.out.println(rt.getRequest().getHeader("aaa"));
		String s = svr.receive();
	//	rt.getResponse().setStatus(500);
		rt.getResponse().setHeader("xxx", "yyy");
		svr.reply("你好："+s);
	}

}
