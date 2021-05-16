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

package figurefix.prac.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.sql.DataSource;

import figurefix.prac.logging.SrcLog;

/**
 * SQL spy tool
 */
public class SqlSpy {

	private static SpyRule spyRule = null;

	/**
	 * set overall spy rule
	 * @param rule spy rule
	 */
	public static synchronized void setRule(SpyRule rule) {
		if(spyRule==null) {
			spyRule = rule;			
		}
	}
	
	private static SrcLog setBypass(SrcLog logger) {
		if(logger.getBypass()==null) {
			return logger.bypass(SqlSpy.class.getPackage().getName());
		} else {
			return logger;
		}
	}

	public static DataSource tap(DataSource ds, String trace) {
		return tap(ds, trace, SrcLog.getDefaultSrcLog());
	}

	public static DataSource tap(DataSource ds, String trace, SrcLog logger) {
		if(ds instanceof TappedDataSource) {
			return ds;
		}
		return new TappedDataSource(ds, trace, setBypass(logger));
	}
	
	public static DataSource tap(DataSource ds, String trace, SpyRule rule) {
		return tap(ds, trace, SrcLog.getDefaultSrcLog(), rule);
	}

	public static DataSource tap(DataSource ds, String trace, SrcLog logger, SpyRule rule) {
		if(ds instanceof TappedDataSource) {
			return ds;
		}
		return new TappedDataSource(ds, trace, setBypass(logger), rule);
	}
	
	/**
	 * this method is not for tapping, 
	 * but for getting a connection that will auto close its statements
	 * @param con connection
	 * @return a connection that will auto close its connection
	 */
	public static Connection tap(Connection con) {
		if(con instanceof TappedConnection) {
			return con;
		}
		return new TappedConnection(con);
	}
	
	public static Connection tap(Connection con, String trace) {
		return tap(con, trace, SrcLog.getDefaultSrcLog());
	}

	public static Connection tap(Connection con, String trace, SrcLog logger) {
		if(con instanceof TappedConnection) {
			return con;
		}
		return new TappedConnection(con, trace, setBypass(logger));
	}

	public static Connection tap(Connection con, String trace, SpyRule rule) {
		return tap(con, trace, SrcLog.getDefaultSrcLog(), rule);
	}

	public static Connection tap(Connection con, String trace, SrcLog logger, SpyRule rule) {
		if(con instanceof TappedConnection) {
			return con;
		}
		return new TappedConnection(con, trace, setBypass(logger), rule);
	}

	static void traceMethod(TappedStatement stm, String method, Object ... args) {
		TappedConnection con = stm.getInterceptConnection();
		if(con.getLogger()==null) {
			return;
		}
		
		String type = null;
		if(stm instanceof CallableStatement) {
			type = CallableStatement.class.getName();
		} else if(stm instanceof PreparedStatement) {
			type = PreparedStatement.class.getName();
		} else {
			type = Statement.class.getName();
		}
		
		SpyRule rule = con.getRule();
		if(rule==null || rule.match(type, method, args)) {
			StringBuilder msg = new StringBuilder();
			msg.append("{SqlSpy:").append(con.getTrace())
				.append("; Conn:").append(Integer.toHexString(con.hashCode()))
				.append("; Stmt:").append(Integer.toHexString(stm.hashCode()))
				.append(":").append(type).append("#").append(method).append("(");
			if(args!=null) {
				for(int i=0; i<args.length; i++) {
					if(i>0) {
						msg.append(",");
					}
					msg.append(args[i].toString());
				}			
			}
			msg.append(")}");
			trace(con.getLogger(), msg.toString());	
		}
	}
	
	static void traceMethod(TappedConnection con, String method, Object ... args) {
		if(con.getLogger()==null) {
			return;
		}
		SpyRule rule = con.getRule();
		if(rule==null || rule.match(Connection.class.getName(), method, args)) {
			StringBuilder msg = new StringBuilder();
			msg.append("{SqlSpy:").append(con.getTrace())
				.append("; Conn:").append(Integer.toHexString(con.hashCode()))
				.append(":").append(Connection.class.getName())
				.append("#").append(method).append("(");
			for(int i=0; i<args.length; i++) {
				if(i>0) {
					msg.append(",");
				}
				msg.append(args[i].toString());
			}
			msg.append(")}");
			trace(con.getLogger(), msg.toString());
		}
	}
	
	private static void traceSql(SqlInfo spyinfo) {
		TappedStatement stm = spyinfo.getStatement();
		TappedConnection con = stm.getInterceptConnection();
		if(con.getLogger()==null) {
			return;
		}
		SpyRule specRule = con.getRule();
		SpyRule finalrule = (specRule!=null ? specRule : spyRule);
		if ( finalrule==null || finalrule.match(spyinfo) ) { // no rule or match rule
			String stype = "Stmt"; //"Statement"; 
			if(spyinfo.getStatement() instanceof TappedCallableStatement) {
				stype = "Callable"; //"CallableStatement";
			} else if(spyinfo.getStatement() instanceof TappedPreparedStatement) {
				stype = "Prepared"; //"PreparedStatement";
			}
			StringBuilder msg = new StringBuilder();
			msg.append("{SqlSpy:").append(con.getTrace())
				.append("; Conn:").append(Integer.toHexString(con.hashCode()))
				.append("; Cost:").append(spyinfo.getCost())
				.append("; ").append(stype).append(":").append(Integer.toHexString(stm.hashCode()))
				.append(":");//.append(spy.sql).append("]");
			if(spyinfo.getSql()!=null) {
				msg.append(spyinfo.getSql()).append("}");
			} else {
				String[] bsqls = spyinfo.getBatchSqls();
				for(int i=0; i<bsqls.length; i++) {
					msg.append("\n").append(bsqls[i]);					
				}
				msg.append("\n}");
			}
			trace(con.getLogger(), msg.toString());
		}
	}
	
	private static void trace(SrcLog logger, String msg) {
		if(logger!=null) {
			logger.traceAnyway(msg.toString());			
		}
	}
	
	static void traceSql(TappedStatement st, String sql, long start) {
		SqlInfo sqlinfo = new SqlInfo(st, sql, System.currentTimeMillis()-start);
		SqlSpy.traceSql(sqlinfo);
	}
	
	static void traceSql(TappedStatement st, String sql, long start, int updated) {
		SqlInfo sqlinfo = new SqlInfo(st, "("+updated+" rows updated) "+sql, System.currentTimeMillis()-start);
		SqlSpy.traceSql(sqlinfo);
	}
	
	static void traceSql(TappedStatement st, String[] sqls, long start) {
		SqlInfo sqlinfo = new SqlInfo(st, sqls, System.currentTimeMillis()-start);
		SqlSpy.traceSql(sqlinfo);
	}
	
}
