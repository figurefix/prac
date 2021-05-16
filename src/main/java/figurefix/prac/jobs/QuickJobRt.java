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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import figurefix.prac.logging.SrcLog;
import figurefix.prac.util.ExpUtil;

public class QuickJobRt extends JobIssuer {
	
	/**
	 * this method is not write in JobService to avoid direct dependency on servlet-api.jar
	 * @param job job instance
	 * @param jrt job runtime
	 */
	static void execute(QuickJob job, QuickJobRt jrt) {
		if(job==null) {
			throw new IllegalArgumentException(QuickJob.class.getName()+" null");
		}
		if(jrt==null) {
			throw new IllegalArgumentException(QuickJobRt.class.getName()+" null");
		}
		
		if(JobService.getConfig().getSID()==null) {
			JobService.getConfig().setSID(
				jrt.getRequest().getLocalAddr()+":"+jrt.getRequest().getLocalPort(),
				false
			);
		}
		
		DBA dba = null;
		boolean doNotCount = false;
		
		try {
			doNotCount = (job instanceof DoNotCount);
			boolean tran = (job instanceof Transactional);
			dba = jrt.getDBA();
			dba.setTransactional(tran);
			job.execute(jrt);
			dba.commit();
			
		} catch(Throwable e) {
			if(dba!=null) {
				dba.rollback();				
			}
			
			if(jrt.getForward()!=null 
			&& jrt.getForward().trim().length()>0 
			&& jrt.getRequest()!=null) {
				if(jrt.getExceptionName()!=null) {
					jrt.getRequest().setAttribute(jrt.getExceptionName(), e);
				} else {
					String name = e.getClass().getName() + "@" + Integer.toHexString(e.hashCode());
					jrt.getRequest().setAttribute(name, e);
				}
			} else {
				SrcLog.error(e);
				if(e instanceof RuntimeException) {
					throw (RuntimeException)e;
				} else {
					throw ExpUtil.toRuntimeException(e);				
				}				
			}
		} finally {
			if(dba!=null) {
				dba.close();
				dba = null;
			}
			
			if( ! doNotCount) {
				JobService.STATISTICS.JOB_COUNT.incrementAndGet();
			}
			
			if(jrt.getForward()!=null 
			&& jrt.getForward().trim().length()>0 
			&& jrt.getRequest()!=null) {
				try {
					jrt.getRequest().getRequestDispatcher(jrt.getForward())
						.forward(jrt.getRequest(), jrt.getResponse());;
				} catch (Throwable e) {
					throw ExpUtil.toRuntimeException(e);
				}
			}
		}
	}

	private String forwardPath = null;
	private String exceptionName = null;
	private HttpServletRequest request = null;
	private HttpServletResponse response = null;
	private HttpServlet servlet = null;
	private DBA dba = null;

	public QuickJobRt(HttpServletRequest req, HttpServletResponse rsp, HttpServlet srv, DataSource ds) {
		this(req, rsp, srv, ds, ds);
	}
	
	public QuickJobRt(HttpServletRequest req, HttpServletResponse rsp, HttpServlet srv, 
			DataSource srvds, DataSource jobds) {
		super(new DBA(srvds));
		if(req==null) {
			throw new IllegalArgumentException(HttpServletRequest.class.getName()+" null");
		}
		if(rsp==null) {
			throw new IllegalArgumentException(HttpServletResponse.class.getName()+" null");
		}
		this.request = req;
		this.response = rsp;
		this.servlet = srv;
		this.dba = (jobds==srvds ? super.getIssuerDBA() : new DBA(jobds));
	}
	
	/**
	 * set the name for thrown exception, 
	 * so the exception will be set into request attribute list with the name.<br>
	 * @param name exception name
	 */
	public void setExceptionName(String name) {
		this.exceptionName = name;
	}
	
	public String getExceptionName() {
		return this.exceptionName;
	}
	
	/**
	 * set the resource path 
	 * where the request will be forwarded to 
	 * after job execution<br>
	 * no mather success or failure for the job execution 
	 * request will always be forwarded to the resource path
	 * @param path resource path
	 */
	public void setForward(String path) {
		this.forwardPath = path;
	}
	
	public String getForward() {
		return this.forwardPath;
	}
	
	public final HttpServletRequest getRequest() {
		return request;
	}

	public final HttpServletResponse getResponse() {
		return response;
	}
	
	public final HttpServlet getServlet() {
		return this.servlet;
	}
	
	public final ServletContext getServletContext() {
		return this.servlet==null ? null : this.servlet.getServletContext();
	}
	
	public final ServletConfig getServletConfig() {
		return this.servlet==null ? null : this.servlet.getServletConfig();
	}
	
	public DBA getDBA() {
		return this.dba;
	}
}
