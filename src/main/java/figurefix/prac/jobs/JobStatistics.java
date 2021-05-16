package figurefix.prac.jobs;

import java.util.concurrent.atomic.AtomicLong;

public class JobStatistics {
	
	public final AtomicLong JOB_COUNT = new AtomicLong(0);
	public final AtomicLong BG_JOB_COUNT = new AtomicLong(0);
	
	public void reset() {
		JOB_COUNT.set(0);
		BG_JOB_COUNT.set(0);
	}
}
