package figurefix.prac.test.taglib;

import java.io.PrintWriter;

public class ThisError extends Exception {
	private static final long serialVersionUID = 1L;

	public ThisError(String msg) {
		super(msg);
	}
	
	public void printStackTrace(PrintWriter s) {
		
	}
}
