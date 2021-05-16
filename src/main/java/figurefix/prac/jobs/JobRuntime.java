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

import figurefix.prac.logging.SrcLog;

public class JobRuntime extends JobIssuer {

	private int batch = -1;
	private String jobUid = null;
	private String param = null;
	private DBA dba = null;

	JobRuntime(DBA srvdba, DBA jobdba) {
		super(srvdba);
		this.dba = jobdba;
	}
	
	public void progress(String msg) {
		if(this.getIssuerDBA()!=null) {
			Connection con = null;
			try {
				con = this.getIssuerDBA().newConnection();
				con.setAutoCommit(true);
				PreparedStatement pst = con.prepareStatement(
					"update "+JobSchema.getEXE()+" set J_LOG=? where J_BAT=? and J_UID=?");
				pst.setString(1, msg);
				pst.setInt(2, this.batch);
				pst.setString(3, this.jobUid);
				pst.executeUpdate();
				pst.close();
			} catch (Exception e) {
				SrcLog.error(e);
			} finally {
				DBA.close(con);
			}
		}
	}
	
	void setParam(String param) {
		this.param = param;
	}

	void setBatch(int batch) {
		this.batch = batch;
	}

	void setJobUid(String jobUid) {
		this.jobUid = jobUid;
	}

	public String getParam() {
		return param;
	}
	
	public DBA getDBA() {
		return this.dba;
	}

	public final int getBatch() {
		return batch;
	}

	public final String getJobUid() {
		return jobUid;
	}
}
