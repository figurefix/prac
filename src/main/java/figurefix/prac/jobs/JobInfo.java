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

public class JobInfo {

	private int batch = JobService.DEFAULT_BATCH;
	private Class<? extends Job> jobclass = null;
	private String parameter = null;
	private String remark = null;
	private int defer = 0; // in seconds

	/**
	 * constructor with batch number 1
	 * @param jobclass background job class
	 */
	public JobInfo(Class<? extends Job> jobclass) {
		this.jobclass = jobclass;
	}
	
	/**
	 * constructor
	 * @param jobclass job class
	 * @param batch batch number (bigger than 0)
	 */
	public JobInfo(Class<? extends Job> jobclass, int batch) {
		this.jobclass = jobclass;
		if(batch<=0) {
			throw new RuntimeException("invalide batch number, positive integer only");
		}
		this.batch = batch;
	}

	public Class<? extends Job> getJobclass() {
		return jobclass;
	}
	
	public int getBatch() {
		return this.batch;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * get defer seconds
	 * @return defer defer seconds
	 */
	public int getDefer() {
		return defer;
	}

	/**
	 * set defer in seconds
	 * @param df defer seconds 
	 */
	public void setDefer(int df) {
		this.defer = df;
	}

}
