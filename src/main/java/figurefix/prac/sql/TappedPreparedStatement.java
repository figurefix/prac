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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;

public class TappedPreparedStatement extends TappedStatement implements PreparedStatement {

	private PreparedStatement st = null;
	private String sql = null;
	private HashMap<Integer, Object> sqlparam = new HashMap<Integer, Object>();
	
	TappedPreparedStatement(PreparedStatement pst, String sql, TappedConnection con) {
		super(pst, con);
		this.st = pst;
		this.sql = sql;
	}
	
	String getSql() {
		return this.sql;
	}
	
	private String getParameterSql() {
		int size = this.sqlparam.size();
		if (size == 0) {
			return this.sql;
		}
		StringBuilder list = new StringBuilder(this.sql);
		for(int i=1; i<=size; i++) {
			list.append(" [").append(i).append("]").append(this.sqlparam.get(i));
		}
		this.sqlparam.clear();
		return list.toString();
	}
	
	@Override
	public ResultSet executeQuery() throws SQLException {
		long start = System.currentTimeMillis();
		try {
			ResultSet rs = this.st.executeQuery();		
			return this.spywrap(rs);
		} finally {
			SqlSpy.traceSql(this, this.getParameterSql(), start);
		}
	}

	@Override
	public int executeUpdate() throws SQLException {
		long start = System.currentTimeMillis();
		int upt = 0;
		try {
			upt = this.st.executeUpdate();
		} finally {
			SqlSpy.traceSql(this, this.getParameterSql(), start, upt);	
		}
		return upt;
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		this.st.setNull(parameterIndex, sqlType);
		this.sqlparam.put(parameterIndex, null);
	}

	@Override
	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		this.st.setBoolean(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setByte(int parameterIndex, byte x) throws SQLException {
		this.st.setByte(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setShort(int parameterIndex, short x) throws SQLException {
		this.st.setShort(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setInt(int parameterIndex, int x) throws SQLException {
		this.st.setInt(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setLong(int parameterIndex, long x) throws SQLException {
		this.st.setLong(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setFloat(int parameterIndex, float x) throws SQLException {
		this.st.setFloat(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setDouble(int parameterIndex, double x) throws SQLException {
		this.st.setDouble(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
		this.st.setBigDecimal(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setString(int parameterIndex, String x) throws SQLException {
		this.st.setString(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		this.st.setBytes(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setDate(int parameterIndex, Date x) throws SQLException {
		this.st.setDate(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x) throws SQLException {
		this.st.setTime(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
		this.st.setTimestamp(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.st.setAsciiStream(parameterIndex, x, length);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	@Deprecated
	public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.st.setUnicodeStream(parameterIndex, x, length);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
		this.st.setBinaryStream(parameterIndex, x, length);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void clearParameters() throws SQLException {
		this.st.clearParameters();
		this.sqlparam.clear();
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
		this.st.setObject(parameterIndex, x, targetSqlType);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setObject(int parameterIndex, Object x) throws SQLException {
		this.st.setObject(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public boolean execute() throws SQLException {
		long start = System.currentTimeMillis();
		try {
			return this.st.execute();			
		} finally {
			SqlSpy.traceSql(this, this.getParameterSql(), start);
		}
	}

	@Override
	public void addBatch() throws SQLException {
		this.st.addBatch();
		this.batlist.add(this.getParameterSql());
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		this.st.setCharacterStream(parameterIndex, reader, length);
		this.sqlparam.put(parameterIndex, reader);
	}

	@Override
	public void setRef(int parameterIndex, Ref x) throws SQLException {
		this.st.setRef(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		this.st.setBlob(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setClob(int parameterIndex, Clob x) throws SQLException {
		this.st.setClob(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setArray(int parameterIndex, Array x) throws SQLException {
		this.st.setArray(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return this.st.getMetaData();
	}

	@Override
	public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
		this.st.setDate(parameterIndex, x, cal);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
		this.st.setTime(parameterIndex, x, cal);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
		this.st.setTimestamp(parameterIndex, x, cal);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.st.setNull(parameterIndex, sqlType, typeName);
		this.sqlparam.put(parameterIndex, null);
	}

	@Override
	public void setURL(int parameterIndex, URL x) throws SQLException {
		this.st.setURL(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this.st.getParameterMetaData();
	}

	@Override
	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		this.st.setRowId(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setNString(int parameterIndex, String value) throws SQLException {
		this.st.setNString(parameterIndex, value);
		this.sqlparam.put(parameterIndex, value);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
		this.st.setNCharacterStream(parameterIndex, value, length);
		this.sqlparam.put(parameterIndex, value);
	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		this.st.setNClob(parameterIndex, value);
		this.sqlparam.put(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.st.setClob(parameterIndex, reader, length);
		this.sqlparam.put(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
		this.st.setBlob(parameterIndex, inputStream, length);
		this.sqlparam.put(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
		this.st.setNClob(parameterIndex, reader, length);
		this.sqlparam.put(parameterIndex, reader);
	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
		this.st.setSQLXML(parameterIndex, xmlObject);
		this.sqlparam.put(parameterIndex, xmlObject);
	}

	@Override
	public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
		this.st.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.st.setAsciiStream(parameterIndex, x, length);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
		this.st.setBinaryStream(parameterIndex, x, length);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
		this.st.setCharacterStream(parameterIndex, reader, length);
		this.sqlparam.put(parameterIndex, reader);
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
		this.st.setAsciiStream(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
		this.st.setBinaryStream(parameterIndex, x);
		this.sqlparam.put(parameterIndex, x);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
		this.st.setCharacterStream(parameterIndex, reader);
		this.sqlparam.put(parameterIndex, reader);
	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
		this.st.setNCharacterStream(parameterIndex, value);
		this.sqlparam.put(parameterIndex, value);
	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		this.st.setClob(parameterIndex, reader);
		this.sqlparam.put(parameterIndex, reader);
	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
		this.st.setBlob(parameterIndex, inputStream);
		this.sqlparam.put(parameterIndex, inputStream);
	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		this.st.setNClob(parameterIndex, reader);
		this.sqlparam.put(parameterIndex, reader);
	}

}
