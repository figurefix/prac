package figurefix.prac.test.logging;

import org.junit.Test;

import figurefix.prac.logging.SrcLog;
import figurefix.prac.test.Commons;

public class TestClearLog {

	@Test
	public void test() {
		
		String file = Commons.getTestHome()+TestClearLog.class.getName()+".log";
		
		SrcLog log1 = new SrcLog(file);
		SrcLog log2 = new SrcLog(file);
		
		log1.logTrace("log1 info");
		log2.logTrace("log2 info");
		log1.logTrace("log3 info");
		
		log1.logError("log1 error");
		log2.logError("log2 error");
		log1.logError(new Exception("last exp"));
	}
}
