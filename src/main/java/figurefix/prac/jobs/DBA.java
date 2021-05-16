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
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import figurefix.prac.logging.SrcLog;
import figurefix.prac.sql.Sqler;
import figurefix.prac.util.DataSet;

/**
 * database tool
 */
public final class DBA {

	private DataSource ds = null;
	private ArrayList<Connection> conlist = null;
	private Connection defaultcon = null;
	private boolean transactional = false;
	private boolean commited = false;
	private boolean dbaClosed = false;
	
	public DBA(DataSource ds) {
		if(ds==null) {
			throw new IllegalArgumentException(DataSource.class.getName()+" null");
		}
		this.ds = ds;
	}
	
	public void setTransactional(boolean tran) throws SQLException {
		this.transactional = tran;
		if(this.defaultcon!=null) {
			this.defaultcon.setAutoCommit(!tran);
		}
	}
	
	/**
	 * open new connection
	 * @return connection
	 * @throws SQLException exp
	 */
	public synchronized Connection newConnection() throws SQLException {
		if(this.dbaClosed) {
			throw new SQLException(DBA.class.getName()+" closed");
		}
		if(ds==null) {
			throw new NullPointerException(DataSource.class.getName());
		}
		if (this.conlist == null) {
			this.conlist = new ArrayList<Connection>();
		}
		Connection con = ds.getConnection();
		this.conlist.add(con);
		return con;
	}
	
	/**
	 * get default connection
	 * @return default connection
	 * @throws SQLException exp
	 */
	public synchronized Connection getConnection() throws SQLException {
		if(this.defaultcon==null) {
			this.defaultcon = this.newConnection();
			if(this.transactional) {
				this.defaultcon.setAutoCommit(false);
			}
		}
		return this.defaultcon;
	}
	
	public static void setAutoCommit(Connection con, boolean autoCommit) {
		try {
			if(con!=null) {
				con.setAutoCommit(autoCommit);
			}
		} catch (Exception e) {
			SrcLog.error(e);
		}
	}

	public static void commit(Connection con) {
		try {
			if(con!=null && !con.getAutoCommit()) {
				con.commit();
			}
		} catch (Exception e) {
			SrcLog.error(e);
		}
	}

	public static void rollback(Connection con) {
		try {
			if(con!=null && !con.getAutoCommit()) {
				con.rollback();				
			}
		} catch (Exception e) {
			SrcLog.error(e);
		}
	}

	public static void close(Connection ... con) {
		if(con!=null) {
			try {
				for(int i=0; i<con.length; i++) {
					if(con[i]!=null) {
						con[i].close();
						con[i] = null;
					}					
				}
			} catch (Exception e) {
				SrcLog.error(e);
			}	
		}
	}
	
	public static void close(ResultSet ... rs) {
		if(rs!=null) {
			try {
				for(int i=0; i<rs.length; i++) {
					if(rs[i]!=null) {
						rs[i].getStatement().close();
						rs[i] = null;						
					}
				}
			} catch (Exception e) {
				SrcLog.error(e);
			}
		}
	}
	
	public static void close(Statement ... st) {
		if(st!=null) {
			try {
				for(int i=0; i<st.length; i++) {
					if(st[i]!=null) {
						st[i].close();
						st[i] = null;
					}
				}
			} catch (Exception e) {
				SrcLog.error(e);
			}		
		}
	}
	
	public void commit() {
		if (!this.commited) {
			commit(this.defaultcon);
			this.commited = true;
		}
	}
	
	public void commitAll() {
		for(int i=0; i<this.conlist.size(); i++) {
			commit(this.conlist.get(i));
		}
	}
	
	public void rollback() {
		rollback(this.defaultcon);
	}
	
	public void rollbackAll() {
		for(int i=0; i<this.conlist.size(); i++) {
			rollback(this.conlist.get(i));
		}
	}
	
	/**
	 * select the first column of the first row from the result set
	 * @param <T> the type of the class that representing the result object
	 * @param type Class representing the result object
	 * @param sql sql statement
	 * @return value
	 * @throws SQLException exception
	 */
	public <T> T select(Class<T> type, String sql) throws SQLException {
		Statement st = null;
		try {
			st = this.getConnection().createStatement();
			ResultSet rs = st.executeQuery(sql);
			return rs.next() ? rs.getObject(1, type) : null;
		} finally {
			DBA.close(st);
			st = null;
		}		
	}
	
	/**
	 * select the first column of the first row from the result set
	 * @param <T> the type of the class that representing the result object
	 * @param type Class representing the result object
	 * @param sql sql statement
	 * @param params parameters of the prepared statement
	 * @return value
	 * @throws SQLException exception
	 */
	public <T> T select(Class<T> type, String sql, Object ... params) throws SQLException {
		PreparedStatement st = this.getConnection().prepareStatement(sql);
		try {
			for(int i=0; i<params.length; i++) {
				st.setObject(i+1, params[i]);
			}
			ResultSet rs = st.executeQuery();
			return rs.next() ? rs.getObject(1, type) : null;
		} finally {
			DBA.close(st);
			st = null;
		}	
	}
	
	/**
	 * select the first row of the result set
	 * @param sql sql statement
	 * @return first row data
	 * @throws SQLException exception
	 */
	public DataSet selectFirst(String sql) throws SQLException {
		Statement st = this.getConnection().createStatement();
		try {
			ResultSet rs = st.executeQuery(sql);
			return rs.next() ? new DataSet(rs) : null;
		} finally {
			DBA.close(st);
			st = null;
		}
	}
	
	/**
	 * select the first row of the result set
	 * @param sql sql statement
	 * @param params parameters of the prepared statement
	 * @return first row data
	 * @throws SQLException exception
	 */
	public DataSet selectFirst(String sql, Object ... params) throws SQLException {
		PreparedStatement st = this.getConnection().prepareStatement(sql);
		try {
			for(int i=0; i<params.length; i++) {
				st.setObject(i+1, params[i]);
			}
			ResultSet rs = st.executeQuery();
			return rs.next() ? new DataSet(rs) : null;
		} finally {
			DBA.close(st);
			st = null;
		}
	}
	
	/**
	 * select data rows from database into the given list
	 * @param list the list to fill
	 * @param sql sql statement
	 * @throws SQLException exception
	 */
	public void select(List<DataSet> list, String sql) throws SQLException {
		this.select(list, 1, 0, sql);
	}
	
	/**
	 * select data rows from database into the given list
	 * @param list the list to fill
	 * @param sql prepared sql statement
	 * @param params parameter for the prepared sql statement
	 * @throws SQLException exception
	 */
	public void select(List<DataSet> list, String sql, Object ... params) 
			throws SQLException {
		this.select(list, 1, 0, sql, params);
	}
	
	/**
	 * select data rows from database into the given list
	 * @param list the list to fill
	 * @param from data row index where begin to read from the result set, starts from 1
	 * @param count read no more than 'count' rows from database
	 * @param sql sql statement
	 * @return true: there are more data to be read in the result set, false: otherwise 
	 * @throws SQLException exception
	 * @see figurefix.prac.util.DataSet#list(List, ResultSet, int, int)
	 */
	public boolean select(List<DataSet> list, int from, int count, String sql) 
			throws SQLException {
		Statement st = this.getConnection().createStatement();
		try {
			ResultSet rs = st.executeQuery(sql);
			return DataSet.list(list, rs, from, count);
		} finally {
			DBA.close(st);
			st = null;
		}
	}
	
	/**
	 * select data rows from database into the given list
	 * @param list the list to fill
	 * @param from data row index where begin to read from the result set, starts from 1
	 * @param count read no more than 'count' rows from database
	 * @param sql prepared sql statement
	 * @param params parameters for the prepared sql statement
	 * @return true: there are more data to be read in the result set, false: otherwise 
	 * @throws SQLException exception
	 * @see figurefix.prac.util.DataSet#list(List, ResultSet, int, int)
	 */
	public boolean select(List<DataSet> list, int from, int count, String sql, Object ... params) 
			throws SQLException {
		PreparedStatement st = this.getConnection().prepareStatement(sql);
		try {
			for(int i=0; i<params.length; i++) {
				st.setObject(i+1, params[i]);
			}
			ResultSet rs = st.executeQuery();
			return DataSet.list(list, rs, from, count);
		} finally {
			DBA.close(st);
			st = null;
		}
	}

	public int execute(String sql) throws SQLException {
		Statement st = this.getConnection().createStatement();
		try {
			return st.executeUpdate(sql);
		} finally {
			DBA.close(st);
			st = null;
		}
	}
	
	/**
	 * execute an prepared statement for update/insert/delete 
	 * @param sql sql statement
	 * @param params paremeters of the prepared statement
	 * @return affected rows
	 * @throws SQLException exception
	 */
	public int execute(String sql, Object ... params) throws SQLException {
		PreparedStatement st = this.getConnection().prepareStatement(sql);
		try {
			for(int i=0; i<params.length; i++) {
				st.setObject(i+1, params[i]);
			}
			return st.executeUpdate();
		} finally {
			DBA.close(st);
			st = null;
		}
	}

	public int insert(String table, DataSet values) throws SQLException {
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(table).append(" (");
		StringBuilder val = new StringBuilder();
		String[] ns = values.getNames();

		boolean comma = false;
		for(int i=0; i<ns.length; i++) {
			String name = ns[i];
			if(values.containsValue(name)) {
				if(comma) {
					sql.append(",");
					val.append(",");
				}
				comma = true;
				sql.append(ns[i]);
				val.append('?');
			}
		}
		sql.append(") values (").append(val).append(')');
		
		PreparedStatement pst = this.getConnection().prepareStatement(sql.toString());	
		try {
			int idx = 1;
			for(int i=0; i<ns.length; i++) {
				Object v = values.getSqlObj(ns[i]);
				if(v!=null) {
					pst.setObject(idx++, v);					
				}
			}
			return pst.executeUpdate();
		} finally {
			DBA.close(pst);
			pst = null;
		}
	}

	public int update(String table, DataSet values) throws SQLException {
		String sql = Sqler.getUpdateStatement(table, values);
		Statement st = this.getConnection().createStatement();
		try {
			return st.executeUpdate(sql);
		} finally {
			DBA.close(st);
			st = null;
		}
	}
	
	/**
	 * close all connections managed by this DBA
	 */
	public synchronized void close() {
		if(this.dbaClosed) {
			return;
		}
		for(int i=0; this.conlist!=null && i<this.conlist.size(); i++) {
			DBA.close(this.conlist.get(i));
		}
		this.dbaClosed = true;
	}
	
	public void finalize() {
		this.close();
	}
}
