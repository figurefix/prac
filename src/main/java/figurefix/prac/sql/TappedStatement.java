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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;

public class TappedStatement implements java.sql.Statement {
	
	private Statement st = null;
	private TappedConnection con = null;
	ArrayList<String> batlist = new ArrayList<String>();
	
	TappedStatement(Statement st, TappedConnection con) {
		this.st = st;
		this.con = con;
	}
	
	TappedConnection getInterceptConnection() {
		return con;
	}
	
	ResultSet spywrap(ResultSet rs) {
		return new TappedResultSet(rs, this);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return st.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return st.isWrapperFor(iface);
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		long start = System.currentTimeMillis();
		try {
			ResultSet rs = st.executeQuery(sql);
			return this.spywrap(rs);
		} finally {
			SqlSpy.traceSql(this, sql, start);
		}
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		long start = System.currentTimeMillis();
		int upt = 0;
		try {
			upt = st.executeUpdate(sql);
		} finally {
			SqlSpy.traceSql(this, sql, start, upt);
		}		
		return upt;
	}

	@Override
	public void close() throws SQLException {
		SqlSpy.traceMethod(this, "close");
		this.st.close();
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		return st.getMaxFieldSize();
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		SqlSpy.traceMethod(this, "setMaxFieldSize", max);
		this.st.setMaxFieldSize(max);
	}

	@Override
	public int getMaxRows() throws SQLException {
		return st.getMaxRows();
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		SqlSpy.traceMethod(this, "setMaxRows", max);
		this.st.setMaxRows(max);
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		SqlSpy.traceMethod(this, "setEscapeProcessing", enable);
		this.st.setEscapeProcessing(enable);
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return st.getQueryTimeout();
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		SqlSpy.traceMethod(this, "setQueryTimeout", seconds);
		this.st.setQueryTimeout(seconds);
	}

	@Override
	public void cancel() throws SQLException {
		SqlSpy.traceMethod(this, "cancel");
		this.st.cancel();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return st.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		SqlSpy.traceMethod(this, "clearWarnings");
		this.st.clearWarnings();
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		SqlSpy.traceMethod(this, "setCursorName", name);
		this.st.setCursorName(name);
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		long start = System.currentTimeMillis();
		try {
			return st.execute(sql);
		} finally {
			SqlSpy.traceSql(this, sql, start);
		}
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		return this.spywrap(st.getResultSet());
	}

	@Override
	public int getUpdateCount() throws SQLException {
		return st.getUpdateCount();
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		return st.getMoreResults();
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		SqlSpy.traceMethod(this, "setFetchDirection", direction);
		this.st.setFetchDirection(direction);
	}

	@Override
	public int getFetchDirection() throws SQLException {
		return st.getFetchDirection();
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		SqlSpy.traceMethod(this, "setFetchSize", rows);
		this.st.setFetchSize(rows);
	}

	@Override
	public int getFetchSize() throws SQLException {
		return st.getFetchSize();
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		return st.getResultSetConcurrency();
	}

	@Override
	public int getResultSetType() throws SQLException {
		return st.getResultSetType();
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		this.st.addBatch(sql);
		this.batlist.add(sql);
	}

	@Override
	public void clearBatch() throws SQLException {
		SqlSpy.traceMethod(this, "clearBatch");
		this.st.clearBatch();
		this.batlist.clear();
	}

	@Override
	public int[] executeBatch() throws SQLException {
		long start = System.currentTimeMillis();
		try {
			return st.executeBatch();
		} finally {
			SqlSpy.traceSql(this, this.batlist.toArray(new String[this.batlist.size()]), start);
			this.batlist.clear();
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.con;
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		return st.getMoreResults(current);
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		return this.spywrap(st.getGeneratedKeys());
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		long start = System.currentTimeMillis();
		int upt = 0;
		try {
			upt = st.executeUpdate(sql, autoGeneratedKeys);
		} finally {
			SqlSpy.traceSql(this, sql, start, upt);
		}
		return upt;
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		long start = System.currentTimeMillis();
		int upt = 0;
		try {
			upt = st.executeUpdate(sql, columnIndexes);
		} finally {
			SqlSpy.traceSql(this, sql, start, upt);
		}
		return upt;
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		long start = System.currentTimeMillis();
		int upt = 0;
		try {
			upt = st.executeUpdate(sql, columnNames);
		} finally {
			SqlSpy.traceSql(this, sql, start, upt);
		}
		return upt;
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		long start = System.currentTimeMillis();
		try {
			return st.execute(sql, autoGeneratedKeys);			
		} finally {
			SqlSpy.traceSql(this, sql, start);
		}
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		long start = System.currentTimeMillis();
		try {
			return st.execute(sql, columnIndexes);			
		} finally {
			SqlSpy.traceSql(this, sql, start);
		}
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		long start = System.currentTimeMillis();
		try {
			return st.execute(sql, columnNames);	
		} finally {
			SqlSpy.traceSql(this, sql, start);
		}
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return st.getResultSetHoldability();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return st.isClosed();
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {
		SqlSpy.traceMethod(this, "setPoolable", poolable);
		this.st.setPoolable(poolable);
	}

	@Override
	public boolean isPoolable() throws SQLException {
		return st.isPoolable();
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		SqlSpy.traceMethod(this, "closeOnCompletion");
		this.st.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return st.isCloseOnCompletion();
	}

}
