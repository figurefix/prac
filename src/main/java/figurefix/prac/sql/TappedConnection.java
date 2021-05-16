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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import figurefix.prac.logging.SrcLog;

public class TappedConnection extends Intercepter implements java.sql.Connection {
	
	private List<Statement> stlist = new ArrayList<Statement>();
	private Connection con = null;
	
	/**
	 * not for tap, but for the auto closing of statements
	 * @param con connection
	 */
	TappedConnection(Connection con) {
		super(null, null, null);
		this.con = con;
	}
	
	TappedConnection(Connection con, String trace, SrcLog logger) {
		super(trace, null, logger);
		this.con = con;
	}
	
	TappedConnection(Connection con, String trace, SrcLog logger, SpyRule rule) {
		super(trace, rule, logger);
		this.con = con;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return this.con.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return this.con.isWrapperFor(iface);
	}

	@Override
	public Statement createStatement() throws SQLException {
		Statement st = new TappedStatement(this.con.createStatement(), this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		PreparedStatement st = new TappedPreparedStatement(this.con.prepareStatement(sql), sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		CallableStatement st = new TappedCallableStatement(this.con.prepareCall(sql), sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		return this.con.nativeSQL(sql);
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		SqlSpy.traceMethod(this, "setAutoCommit", autoCommit);
		this.con.setAutoCommit(autoCommit);
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return this.con.getAutoCommit();
	}

	@Override
	public void commit() throws SQLException {
		SqlSpy.traceMethod(this, "commit");
		this.con.commit();
	}

	@Override
	public void rollback() throws SQLException {
		SqlSpy.traceMethod(this, "rollback");
		this.con.rollback();
	}

	@Override
	public void close() throws SQLException {
		SqlSpy.traceMethod(this, "close");
		for(Statement st : this.stlist) {
			st.close();
		}
		this.stlist.clear();
		this.con.close();
	}

	@Override
	public boolean isClosed() throws SQLException {
		return this.con.isClosed();
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return this.con.getMetaData();
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		SqlSpy.traceMethod(this, "setReadOnly", readOnly);
		this.con.setReadOnly(readOnly);
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return this.con.isReadOnly();
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		SqlSpy.traceMethod(this, "setCatalog", catalog);
		this.con.setCatalog(catalog);
	}

	@Override
	public String getCatalog() throws SQLException {
		return this.con.getCatalog();
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		SqlSpy.traceMethod(this, "setTransactionIsolation", level);
		this.con.setTransactionIsolation(level);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		return this.con.getTransactionIsolation();
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		return this.con.getWarnings();
	}

	@Override
	public void clearWarnings() throws SQLException {
		SqlSpy.traceMethod(this, "clearWarnings");
		this.con.clearWarnings();
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		Statement st = new TappedStatement(this.con.createStatement(resultSetType, resultSetConcurrency), this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
			throws SQLException {
		PreparedStatement st = new TappedPreparedStatement(
				this.con.prepareStatement(sql, resultSetType, resultSetConcurrency), 
				sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		CallableStatement st = new TappedCallableStatement(
				this.con.prepareCall(sql, resultSetType, resultSetConcurrency), 
				sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return this.con.getTypeMap();
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		SqlSpy.traceMethod(this, "setTypeMap", map);
		this.con.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		SqlSpy.traceMethod(this, "setHoldability", holdability);
		this.con.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		return this.con.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		SqlSpy.traceMethod(this, "setSavepoint");
		return this.con.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		SqlSpy.traceMethod(this, "setSavepoint", name);
		return this.con.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		SqlSpy.traceMethod(this, "rollback", savepoint);
		this.con.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		SqlSpy.traceMethod(this, "releaseSavepoint", savepoint);
		this.con.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(
			int resultSetType, int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		Statement st = new TappedStatement(
				this.con.createStatement(
						resultSetType, resultSetConcurrency, resultSetHoldability), 
				this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public PreparedStatement prepareStatement(
			String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) 
			throws SQLException {
		PreparedStatement st = new TappedPreparedStatement(
				this.con.prepareStatement(
						sql, resultSetType, resultSetConcurrency, resultSetHoldability), 
				sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
			int resultSetHoldability) throws SQLException {
		CallableStatement st = new TappedCallableStatement(
				this.con.prepareCall(
						sql, resultSetType, resultSetConcurrency, resultSetHoldability), 
				sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) 
			throws SQLException {
		PreparedStatement st = new TappedPreparedStatement(
				this.con.prepareStatement(sql, autoGeneratedKeys), 
				sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) 
			throws SQLException {
		PreparedStatement st = new TappedPreparedStatement(
				this.con.prepareStatement(sql, columnIndexes), 
				sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) 
			throws SQLException {
		PreparedStatement st = new TappedPreparedStatement(
				this.con.prepareStatement(sql, columnNames), 
				sql, this);
		this.stlist.add(st);
		return st;
	}

	@Override
	public Clob createClob() throws SQLException {
		return this.con.createClob();
	}

	@Override
	public Blob createBlob() throws SQLException {
		return this.con.createBlob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return this.con.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return this.con.createSQLXML();
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return this.con.isValid(timeout);
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {
		SqlSpy.traceMethod(this, "setClientInfo", name, value);
		this.con.setClientInfo(name, value);
	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {
		SqlSpy.traceMethod(this, "setClientInfo", properties);
		this.con.setClientInfo(properties);
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return this.con.getClientInfo(name);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return this.con.getClientInfo();
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return this.con.createArrayOf(typeName, elements);
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return this.con.createStruct(typeName, attributes);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		SqlSpy.traceMethod(this, "setSchema", schema);
		this.con.setSchema(schema);
	}

	@Override
	public String getSchema() throws SQLException {
		return this.con.getSchema();
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		SqlSpy.traceMethod(this, "abort", executor);
		this.con.abort(executor);
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
		SqlSpy.traceMethod(this, "setNetworkTimeout", executor, milliseconds);
		this.con.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return this.con.getNetworkTimeout();
	}

}
