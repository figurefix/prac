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

import javax.sql.DataSource;

import figurefix.prac.logging.SrcLog;
import figurefix.prac.util.DateTime;
import figurefix.prac.util.Utilx;

public final class Worker extends Thread {

	private int batch = -1;
	private String jobuid = null;
	
	Worker(int batch, String juid) {
		this.batch = batch;
		this.jobuid = juid;
	}
	
	public void run() {

		DateTime start = DateTime.now();
		
		DBA srvdba1 = new DBA(JobService.getConfig().getSrvDataSource());
		Connection con = null;
		String clsname = null;
		String jobparam = null;
		long timpln = 0;
		PreparedStatement selpst = null;
		try {
			con = srvdba1.newConnection();
			con.setAutoCommit(true);
			selpst = con.prepareStatement(
				"select * from "+JobSchema.getEXE()+" where J_BAT=? and J_UID=?");
			selpst.setInt(1, this.batch);
			selpst.setString(2, this.jobuid);
			ResultSet jobrs = selpst.executeQuery();
			if(!jobrs.next()) {
				return;
			}
			
			clsname = jobrs.getString("J_CLS");
			jobparam = jobrs.getString("J_PAR");
			timpln = jobrs.getLong("J_TIMPLN");

		} catch (Throwable e) {
			SrcLog.error(e);
			return;
		} finally {
			DBA.close(selpst);
			selpst = null;
			srvdba1.close();
			srvdba1 = null;
		}
		
		if(timpln<0) { // short defer that needs to be deferred by worker thread
			long sleep = 0 - timpln - System.currentTimeMillis();
			if(sleep>0) {
				SrcLog.trace(JobService.getConfig().getSrvMark(), 
						" short defer for ", sleep, "ms (worker:", this.hashCode(), 
						", batch:", this.batch, ", uid:", this.jobuid, ")");
				Utilx.threadSleep(sleep);
			}
		}
		
		boolean doNotCount = false;
		String err = null;
		
		DataSource srvds = JobService.getConfig().getSrvDataSource();
		DataSource jobds = JobService.getConfig().getJobDataSource();
		DBA srvdba = new DBA(srvds);
		DBA jobdba = srvds==jobds ? srvdba : new DBA(jobds);
		JobRuntime rt = new JobRuntime(srvdba, jobdba);
		rt.setBatch(this.batch);
		rt.setJobUid(this.jobuid);
		rt.setParam(jobparam);
		
		try {

			Class<?> cls = Class.forName(clsname);
			Job job = (Job)(cls.newInstance());
			doNotCount = (job instanceof DoNotCount);
			boolean tran = (job instanceof Transactional);
			jobdba.setTransactional(tran);
			
			job.execute(rt);
			jobdba.commit();
			
			cls = null;
			job = null;

		} catch (Throwable e) {
			jobdba.rollback();
			err = e.toString();
			SrcLog.error(e);
		} finally {
			if( ! doNotCount) {
				JobService.STATISTICS.BG_JOB_COUNT.incrementAndGet();
			}
			jobdba.close();
			jobdba = null;
		}

		DateTime end = DateTime.now();
		int sta = err==null ? JobState.DONE.intValue() : JobState.ERROR.intValue();
		
		DBA srvdba2 = new DBA(JobService.getConfig().getSrvDataSource());
		PreparedStatement endpst = null;
		PreparedStatement inspst = null;
		PreparedStatement delpst = null;
		try {
			con = srvdba2.newConnection();
			con.setAutoCommit(false);
			
			endpst = con.prepareStatement(
				"update "+JobSchema.getEXE()
				+ " set J_STA=?, J_TIMEND=?, J_TIM=?, J_ERR=?"
				+ " where J_BAT=? and J_UID=?");
			int i = 1;
			endpst.setInt(i++, sta);
			endpst.setString(i++, end.toString());
			endpst.setLong(i++, end.getTimeMillis() - start.getTimeMillis());
			endpst.setString(i++, err);
			endpst.setInt(i++, this.batch);
			endpst.setString(i++, this.jobuid);
			
			inspst = con.prepareStatement(
				"INSERT INTO "+JobSchema.getHIS()+
				" select * from "+JobSchema.getEXE()+
				" where J_BAT=? and J_UID=?");
			inspst.setInt(1, this.batch);
			inspst.setString(2, this.jobuid);
			
			delpst = con.prepareStatement(
				" delete from "+JobSchema.getEXE()+
				" where J_BAT=? and J_UID=?");
			delpst.setInt(1, this.batch);
			delpst.setString(2, this.jobuid);
			
			endpst.executeUpdate();
			inspst.executeUpdate();
			delpst.executeUpdate();
			
			con.commit();
			
		} catch (Throwable e) {
			DBA.rollback(con);
			SrcLog.error(e);
		} finally {
			
			DBA.close(endpst, inspst, delpst);
			endpst = null;
			inspst = null;
			delpst = null;
			
			srvdba2.close();
			srvdba2 = null;
		}
		
		JobService.rlsWorker(this.batch, this.jobuid);

	}
}
