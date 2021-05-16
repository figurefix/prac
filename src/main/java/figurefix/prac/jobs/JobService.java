/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining 
 * a copy of this software and associated documentation files (the 
 * "Software"), to deal in the Software without restriction, including 
 * without limitation the rights to use, copy, modify, merge, publish, 
 * distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to 
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF 
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION 
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION 
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */

package figurefix.prac.jobs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import figurefix.prac.logging.SrcLog;
import figurefix.prac.util.DataSet;
import figurefix.prac.util.DateTime;
import figurefix.prac.util.Utilx;

public final class JobService extends Thread {
	
	public static void execute(QuickJob job, QuickJobRt jrt) {
		QuickJobRt.execute(job, jrt);
	}
	
	public static final JobStatistics STATISTICS = new JobStatistics();
	public static final int DEFAULT_BATCH = 1;
	private static JobConfig jobConfig = new JobConfig(); // default config
	private static JobService mWorker = null;
	private static HashMap<String, Worker> workers = new HashMap<String, Worker>();
	private static class WorkerLocker {}
	
	static JobConfig getConfig() {
		return jobConfig;
	}
	
	/**
	 * start background job service
	 * @param ds datasource
	 * @param sid a unique identifier for this service instance
	 */
	public static synchronized void startup(DataSource ds, String sid) {
		JobConfig jfg = new JobConfig(ds, sid);
		startup(jfg);
	}

	public static synchronized void startup(JobConfig jfg) {
		if(jfg==null) {
			throw new IllegalArgumentException(JobConfig.class.getName()+" null");
		}
		JobService.jobConfig = jfg;
		try {
			if(mWorker!=null && mWorker.isAlive()) {
				return;
			}
			mWorker = null;
			mWorker = new JobService();
			mWorker.running.set(true);
			mWorker.start();
		} catch (Throwable e) {
			SrcLog.error(e);
		}
	}
	
	static boolean isAvailable() {
		return (mWorker!=null && mWorker.isAlive()); 
	}
	
	public static synchronized void shutdown() {
		if(mWorker!=null) {
			SrcLog.info(JobService.getConfig().getSrvMark()+
					" shutting down ... (remaining workers:"+workers.size()+")");
			mWorker.running.set(false);
			proceed(); // wake up to quit
			while(workers.size()>0 || mWorker.isAlive()) {
				Utilx.threadSleep(3000);
				if(workers.size()>0) {
					SrcLog.info(JobService.getConfig().getSrvMark()+
							" shutting down ... (remaining workers:"+workers.size()+")");					
				}
				if(mWorker.running.get()) { // maybe started again
					SrcLog.info(JobService.getConfig().getSrvMark()+" resumed");
					return;
				}
			}
			SrcLog.info(JobService.getConfig().getSrvMark()+" has shut down!");
		}
	}
	
	/**
	 * wake up job service from idleness
	 */
	public static void proceed() {
		SrcLog.trace(JobService.getConfig().getSrvMark(), " proceeding ...");
		synchronized(WorkerLocker.class) {
			WorkerLocker.class.notifyAll();
		}
	}
	
	static void addWorker(int batch, String jobuid, Worker w) {
		synchronized(workers) {
			workers.put(batch+"."+jobuid, w);
		}
	}
	
	static void rlsWorker(int batch, String jobuid) {
		synchronized(workers) {
			workers.remove(batch+"."+jobuid);
		}
		if( ! mWorker.hasMoreWorkerLately) {
			JobService.proceed();
		}
	}
	
	/**
	 * count active workers
	 * @return worker count
	 */
	public static int countWorkers() {
		return workers.size();
	}
	
	/**
	 * get executing job state from this job service instance
	 * @param batch batch
	 * @param jobuid job uid
	 * @return 0: not found, 1: executing, 2: else
	 */
	public static int getJobState(int batch, String jobuid) {
		Worker w = workers.get(batch+"."+jobuid);
		if(w==null) {
			return 0;
		} else if(w.isAlive()) {
			return 1;
		} else {
			return 2;
		}
	}
	
	/**
	 * get job state by querying database.(use default batch number)
	 * @param dba DBA
	 * @param jobuid job uid
	 * @return state
	 * @throws SQLException exception
	 */
	public static JobState getJobState(DBA dba, String jobuid) throws SQLException {
		return getJobState(dba, JobService.DEFAULT_BATCH, jobuid);
	}
	
	/**
	 * get job state by querying database
	 * @param dba DBA 
	 * @param batch batch number
	 * @param jobuid job uid
	 * @return state
	 * @throws SQLException exception
	 */
	public static JobState getJobState(DBA dba, int batch, String jobuid) throws SQLException {
		
		if(batch<=0) {
			throw new RuntimeException("invalide batch number");
		}
		
		Connection con = dba.getConnection();
		Statement st = con.createStatement();
		String where = " where J_BAT='"+batch+"' and J_UID='"+jobuid+"'";
		ResultSet rs = st.executeQuery("select * from "+JobSchema.getEXE()+where);
		JobState state = null;
		if(rs.next()) {
			state = new JobState();
		} else {
			rs = st.executeQuery("select * from "+JobSchema.getHIS()+where);
			if(rs.next()) {
				state = new JobState();
			}
		}
		if(state!=null) {
			state.setParam(rs.getString("J_PAR"));
			state.setState(rs.getInt("J_STA"));
			state.setProgress(rs.getString("J_LOG"));
			state.setErrMsg(rs.getString("J_ERR"));			
		}
		
		st.close();
		st = null;
		return state;
	}
	
	private static int sno = 0;
	
	private static synchronized int nextsno() {
		if(sno>=1000) {
			sno = 0;
		}
		return sno++;
	}
	
//	public static String issue(DataSource ds, JobInfo jobinfo) throws SQLException {
//		DBA dba = new DBA(ds);
//		try {
//			String jid = issue(dba, jobinfo);
//			return jid;			
//		} finally {
//			dba.close();
//		}
//	}
	
	/**
	 * keep transaction consistency with the input DBA
	 * @param dba DBA
	 * @param jobinfo job info
	 * @return job uid
	 * @throws SQLException exception
	 */
	public static String issue(DBA dba, JobInfo jobinfo) throws SQLException {

		DateTime time = DateTime.now();
		StringBuilder uid = new StringBuilder();
		uid.append(Long.toHexString(time.getTimeMillis()).toUpperCase())
			.append(nextsno())
			.append(JobService.getConfig().getSID());

		DataSet dm = new DataSet();
		
		long plan = 0;
		if(jobinfo.getDefer()>0) {
			plan = time.getTimeMillis()+jobinfo.getDefer()*1000;
			if(jobinfo.getDefer() <= JobService.getConfig().getShortDefer() 
			&& (jobinfo.getDefer())*1000 < JobService.getConfig().getWorkerThreadIdleness()) {
				plan = 0-plan; // negative
			}
		}
		
		dm.set("J_BAT", jobinfo.getBatch());
		dm.set("J_UID", uid.toString());
		if(plan<=0) {
			dba.insert(JobSchema.getISU(), dm);
		} else {
			SrcLog.trace("issue job plan, (defer:", jobinfo.getDefer(), 
					", param:", jobinfo.getParameter(), ")");
			dm.set("J_TIMPLN", plan); // set for JOBS_PLN
			dba.insert(JobSchema.getPLN(), dm);			
		}
		
		dm.set("J_TIMPLN", plan); // set for JOBS_EXE
		dm.set("J_CLS", jobinfo.getJobclass().getName());
		dm.set("J_PAR", jobinfo.getParameter());
		dm.set("J_STA", JobState.READY.intValue());
		dm.set("J_TIMREG", time.toString());
		dm.set("J_RMK", jobinfo.getRemark());
		dba.insert(JobSchema.getEXE(), dm);
		
		return uid.toString();
	}
	
	public static void clean(DBA dba, int batch, String jobuid) throws SQLException {
		Statement st = dba.getConnection().createStatement();
		String where = " WHERE J_BAT="+batch+" AND J_SID='"+jobuid+"'";
		st.addBatch("delete from JOBS_ISU"+where);
		st.addBatch("delete from JOBS_PLN"+where);
		st.addBatch("delete from JOBS_EXE"+where);
		st.executeBatch();
		st.close();
	}
	
	private final AtomicBoolean running = new AtomicBoolean(false);
	private int sleepTime; //milliseconds
	private boolean hasMoreWorkerLately = true;
	
	private JobService() {

	}
	
	private synchronized boolean hasMoreWorker() {
		
		int wmax = JobService.jobConfig.getMaxWorkerThreads();
		if(wmax==0) {
			SrcLog.trace("no more workers, max worker threads 0");
			return false;
		}
		if(wmax>0) {
			int wnow = JobService.workers.size();
			if(wnow>=wmax) {
				SrcLog.trace("no more workers, threads:", wnow, ", max:", wmax);
				return false;
			}
		}

		int pct = JobService.jobConfig.getFreeMemoryPercent();
		if(pct==100) {
			SrcLog.trace("no more workers, free memory config 100%");
			return false;
		}
		if(pct>=1 && pct<100) {
			Runtime rt = Runtime.getRuntime();
			long nowmem = rt.totalMemory();
			long maxmem = rt.maxMemory();
			long freemem = maxmem - (nowmem - rt.freeMemory());
			if( freemem <= (maxmem/100*pct) ) {
				SrcLog.trace("no more workers, free mem:", freemem, ", max mem:", maxmem);
				return false;
			}
		}
		
		return true;
	}
	
	public void run() {

		int wmax = JobService.jobConfig.getMaxWorkerThreads();
		if(wmax == 0) {
			SrcLog.info(JobService.getConfig().getSrvMark()+
					" not available (MaxWorkerThreads:"+wmax+").");
			return;
		}
		
		SrcLog.info(JobService.getConfig().getSrvMark()+" available.");
		
		while(this.running.get()) {
			
			this.sleepTime = JobService.jobConfig.getWorkerThreadIdleness();
			if(this.sleepTime<0) {
				this.sleepTime = 60000;
			}

			SrcLog.trace(JobService.getConfig().getSrvMark(), " functioning");
			this.worknow();	
			
			if(this.sleepTime>0) {
				try {
					synchronized(WorkerLocker.class) {
						WorkerLocker.class.wait(this.sleepTime);
					}
				} catch (Exception e) {
					continue;
				}				
			}
		}
		
		SrcLog.trace(JobService.getConfig().getSrvMark()+" main thread quit");
	}
	
	private synchronized void worknow() {
		this.hasMoreWorkerLately = this.hasMoreWorker();
		if(this.hasMoreWorkerLately) {
			DBA dba = null;
			try {
				dba = new DBA(JobService.jobConfig.getSrvDataSource());
				this.work(dba);
			} catch (Throwable e) {
				SrcLog.error(e);
			} finally {
				dba.close();
				dba = null;
			}
		}
	}
	
	private void updatePlans(Connection con) throws Exception {
		
		DateTime now = DateTime.now();
		
		PreparedStatement dfmpst0 = null;
		PreparedStatement dfmpst1 = null;
		PreparedStatement dfmpst2 = null;
		try {
			
			con.setAutoCommit(false);

			String lck = JobService.getConfig().getSID();
			dfmpst0 = con.prepareStatement(
				"update "+JobSchema.getPLN()+" set J_SID=? where J_SID is NULL and J_TIMPLN<=?");
			dfmpst0.setString(1, lck);
			dfmpst0.setLong(2, now.getTimeMillis());
			int set0 = dfmpst0.executeUpdate();
			if(set0>0) {
				dfmpst1 = con.prepareStatement(
					"insert into "+JobSchema.getISU()
					+" select J_BAT,J_UID from "+JobSchema.getPLN()+" where J_SID=?");
				dfmpst1.setString(1, lck);
				dfmpst2 = con.prepareStatement(
					"delete from "+JobSchema.getPLN()+" where J_SID=?");
				dfmpst2.setString(1, lck);
				
				dfmpst1.executeUpdate();
				dfmpst2.executeUpdate();
			}
			
			con.commit();
			
		} catch (Throwable e) {
			DBA.rollback(con);
			SrcLog.error(e);
		} finally {
			DBA.close(dfmpst0, dfmpst1, dfmpst2);
			dfmpst0 = null;
			dfmpst1 = null;
			dfmpst2 = null;
		}
	}
	
	private void work(DBA dba) throws Exception {
		
		Connection con = dba.getConnection();
		con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

		Integer batch = dba.select(Integer.class, 
			"select J_VAL from "+JobSchema.getCFG()+" where J_KEY='batch'");
		if(batch==null) {
			batch = JobService.DEFAULT_BATCH;
		}
		if(batch==0) {
			return; // pause
		}
		
		this.updatePlans(con);

		con.setAutoCommit(true);
		
		String uid = dba.select(String.class, 
			"select min(J_UID) from "+JobSchema.getISU()+" where J_BAT=?", batch);
		if(uid==null) {
			return; // no job
		}

		this.sleepTime = 0; //found job, do not sleep
		
		SrcLog.trace("got job (batch:", batch, ", uid:", uid, ")");
		
		DateTime start = DateTime.now();
		
		int started = dba.execute("update "+JobSchema.getEXE()+
				" set J_STA=?, J_TIMEXE=?, J_SID=?"+
				" where J_BAT=? and J_UID=? and J_STA=?", 
				JobState.EXE.intValue(), start.toString(), JobService.getConfig().getSID(), 
				batch, uid, JobState.READY.intValue());
		
		if(started==1) { // caught a job
			Worker w = new Worker(batch, uid);
			JobService.addWorker(batch, uid, w);
			w.start();
		}
		
		dba.execute("delete from "+JobSchema.getISU()+" where J_BAT=? and J_UID=?", batch, uid);
	}
	
	/**
	 * unit test for a single job
	 * @param ds datasource
	 * @param cls Job class
	 * @param param parameter
	 */
	public static void test(DataSource ds, Class<? extends Job> cls, String param) {
		test(ds, ds, JobService.DEFAULT_BATCH, "test.jobuid", cls, param);
	}
	
	/**
	 * unit test for a single job
	 * @param srvds service datasource
	 * @param jobds job datasource
	 * @param batch batch number
	 * @param jobuid job uid
	 * @param cls Job class
	 * @param param parameter
	 */
	public static void test(DataSource srvds, DataSource jobds, 
			int batch, String jobuid, Class<? extends Job> cls, String param) {
		DBA srvdba = new DBA(srvds);
		DBA jobdba = srvds==jobds ? srvdba : new DBA(jobds);
		JobRuntime rt = new JobRuntime(jobdba, srvdba);
		rt.setBatch(batch);
		rt.setJobUid(jobuid);
		rt.setParam(param);
		
		try {
			Job job = (Job)(cls.newInstance());
			boolean tran = (job instanceof Transactional);
			jobdba.setTransactional(tran);
			
			job.execute(rt);
			jobdba.commit();

		} catch (Throwable e) {
			jobdba.rollback();
			SrcLog.error(e);
		}
	}
}
