package figurefix.prac.test.httpmsg;

import org.junit.Test;

import figurefix.prac.httpmsg.HttpRequest;

public class TestHttpRequest {

	@Test
	public void testPost() throws Exception {
		boolean ssl = false;
		String url = "http"+(ssl?"s":"")
				+"://127.0.0.1:"
				+(ssl?"8443":"8080")
				+"/practest/testHttpMessageJob.do";
		HttpRequest cli = new HttpRequest(url);
		cli.getConnection().setRequestProperty("aaa", "bbb");
		String ss = cli.post("张三");
		System.out.println(ss);	
	}
}
