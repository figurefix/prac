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

import javax.sql.DataSource;

public final class JobConfig {
	
	private String sid = null;
	private String srvMark = null;
	private DataSource jobDataSource = null;
	private DataSource srvDataSource = null;
	
	// variables that have default value
	private String schema = null;
	private int freeMemoryPercent = 30; // per cent
	private int maxWorkerThreads = -1;
	private int workerThreadIdleness = 60000; // milliseconds
	private int shortDefer = 30; // seconds
	
	JobConfig() {
		
	}

	public JobConfig(DataSource ds, String sid) {
		this(ds, ds, sid);
	}
	
	/**
	 * constructor
	 * @param srvDs job service datasource
	 * @param jobDs job datasource
	 * @param sid service id
	 */
	public JobConfig(DataSource srvDs, DataSource jobDs, String sid) {
		if(jobDs==null || srvDs==null) {
			throw new IllegalArgumentException(DataSource.class.getName()+" null");
		}
		if(sid==null || sid.trim().length()==0) {
			throw new IllegalArgumentException("sid absent");
		}		
		this.jobDataSource = jobDs;
		this.srvDataSource = srvDs;
		this.sid = sid;
		this.updateSvrMark();
	}
	
	public String getSID() {
		return this.sid;
	}
	
	void setSID(String sid, boolean override) {
		if(sid!=null && sid.trim().length()>0 
		&& (override || this.sid==null)) {
			this.sid = sid;
			this.updateSvrMark();
		}
	}
	
	private void updateSvrMark() {
		this.srvMark = "job service (sid:"+this.getSID()+")";
	}
	
	String getSrvMark() {
		return this.srvMark;
	}

	public String getSchema() {
		return schema;
	}

	/**
	 * set schema for JOBS_* tables
	 * @param schema Schema
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	public int getFreeMemoryPercent() {
		return freeMemoryPercent;
	}

	public void setFreeMemoryPercent(int freeMemory) {
		this.freeMemoryPercent = freeMemory;
	}

	public int getMaxWorkerThreads() {
		return maxWorkerThreads;
	}

	public void setMaxWorkerThreads(int maximumWorkerThreads) {
		this.maxWorkerThreads = maximumWorkerThreads;
	}

	public int getWorkerThreadIdleness() {
		return workerThreadIdleness;
	}

	/**
	 * set worker idleness in milliseconds
	 * @param workerThreadIdleness idleness in milliseconds
	 */
	public void setWorkerThreadIdleness(int workerThreadIdleness) {
		this.workerThreadIdleness = workerThreadIdleness;
	}

	public DataSource getJobDataSource() {
		return jobDataSource;
	}
	
	public DataSource getSrvDataSource() {
		return srvDataSource;
	}

	public int getShortDefer() {
		return shortDefer;
	}

	/**
	 * set the value of short defer in seconds, 
	 * jobs that defers short than this value will be executed directly and defers by thread sleep
	 * @param shortDefer seconds
	 */
	public void setShortDefer(int shortDefer) {
		this.shortDefer = shortDefer;
	}
	
}
