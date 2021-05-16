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
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public class TappedCallableStatement extends TappedPreparedStatement implements CallableStatement {

	static class TmpMap {
		public void put(String key, Object val) {
			// InterceptCallableStatement 暂不实现
		}
	}
	
	private CallableStatement st = null;
//	private HashMap<String, Object> callparam = new HashMap<String, Object>();
	private TmpMap callparam = new TmpMap();

	
	TappedCallableStatement(CallableStatement st, String sql, TappedConnection con) {
		super(st, sql, con);
		this.st = st;
	}
	
	@Override
	public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
		this.st.registerOutParameter(parameterIndex, sqlType);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
		this.st.registerOutParameter(parameterIndex, sqlType, scale);
	}

	@Override
	public boolean wasNull() throws SQLException {
		return this.st.wasNull();
	}

	@Override
	public String getString(int parameterIndex) throws SQLException {
		return this.st.getString(parameterIndex);
	}

	@Override
	public boolean getBoolean(int parameterIndex) throws SQLException {
		return this.st.getBoolean(parameterIndex);
	}

	@Override
	public byte getByte(int parameterIndex) throws SQLException {
		return this.st.getByte(parameterIndex);
	}

	@Override
	public short getShort(int parameterIndex) throws SQLException {
		return this.st.getShort(parameterIndex);
	}

	@Override
	public int getInt(int parameterIndex) throws SQLException {
		return this.st.getInt(parameterIndex);
	}

	@Override
	public long getLong(int parameterIndex) throws SQLException {
		return this.st.getLong(parameterIndex);
	}

	@Override
	public float getFloat(int parameterIndex) throws SQLException {
		return this.st.getFloat(parameterIndex);
	}

	@Override
	public double getDouble(int parameterIndex) throws SQLException {
		return this.st.getDouble(parameterIndex);
	}

	@Override
	@Deprecated
	public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
		return this.st.getBigDecimal(parameterIndex, scale);
	}

	@Override
	public byte[] getBytes(int parameterIndex) throws SQLException {
		return this.st.getBytes(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex) throws SQLException {
		return this.st.getDate(parameterIndex);
	}

	@Override
	public Time getTime(int parameterIndex) throws SQLException {
		return this.st.getTime(parameterIndex);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return this.st.getTimestamp(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex) throws SQLException {
		return this.st.getObject(parameterIndex);
	}

	@Override
	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return this.st.getBigDecimal(parameterIndex);
	}

	@Override
	public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
		return this.st.getObject(parameterIndex, map);
	}

	@Override
	public Ref getRef(int parameterIndex) throws SQLException {
		return this.st.getRef(parameterIndex);
	}

	@Override
	public Blob getBlob(int parameterIndex) throws SQLException {
		return this.st.getBlob(parameterIndex);
	}

	@Override
	public Clob getClob(int parameterIndex) throws SQLException {
		return this.st.getClob(parameterIndex);
	}

	@Override
	public Array getArray(int parameterIndex) throws SQLException {
		return this.st.getArray(parameterIndex);
	}

	@Override
	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return this.st.getDate(parameterIndex, cal);
	}

	@Override
	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return this.st.getTime(parameterIndex, cal);
	}

	@Override
	public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
		return this.st.getTimestamp(parameterIndex, cal);
	}

	@Override
	public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
		this.st.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
		this.st.registerOutParameter(parameterName, sqlType);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
		this.st.registerOutParameter(parameterName, sqlType, scale);
	}

	@Override
	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
		this.st.registerOutParameter(parameterName, sqlType, typeName);
	}

	@Override
	public URL getURL(int parameterIndex) throws SQLException {
		return this.st.getURL(parameterIndex);
	}

	@Override
	public void setURL(String parameterName, URL val) throws SQLException {
		this.st.setURL(parameterName, val);
		this.callparam.put(parameterName, val);
	}

	@Override
	public void setNull(String parameterName, int sqlType) throws SQLException {
		this.st.setNull(parameterName, sqlType);
		this.callparam.put(parameterName, null);
	}

	@Override
	public void setBoolean(String parameterName, boolean x) throws SQLException {
		this.st.setBoolean(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setByte(String parameterName, byte x) throws SQLException {
		this.st.setByte(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setShort(String parameterName, short x) throws SQLException {
		this.st.setShort(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setInt(String parameterName, int x) throws SQLException {
		this.st.setInt(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setLong(String parameterName, long x) throws SQLException {
		this.st.setLong(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setFloat(String parameterName, float x) throws SQLException {
		this.st.setFloat(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setDouble(String parameterName, double x) throws SQLException {
		this.st.setDouble(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
		this.st.setBigDecimal(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setString(String parameterName, String x) throws SQLException {
		this.st.setString(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setBytes(String parameterName, byte[] x) throws SQLException {
		this.st.setBytes(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setDate(String parameterName, Date x) throws SQLException {
		this.st.setDate(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setTime(String parameterName, Time x) throws SQLException {
		this.st.setTime(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
		this.st.setTimestamp(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
		this.st.setAsciiStream(parameterName, x, length);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
		this.st.setBinaryStream(parameterName, x, length);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
		this.st.setObject(parameterName, x, targetSqlType, scale);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
		this.st.setObject(parameterName, x, targetSqlType);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setObject(String parameterName, Object x) throws SQLException {
		this.st.setObject(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException {
		this.st.setCharacterStream(parameterName, reader, length);
		this.callparam.put(parameterName, reader);
	}

	@Override
	public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
		this.st.setDate(parameterName, x, cal);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
		this.st.setTime(parameterName, x, cal);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
		this.st.setTimestamp(parameterName, x, cal);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
		this.st.setNull(parameterName, sqlType, typeName);
		this.callparam.put(parameterName, null);
	}

	@Override
	public String getString(String parameterName) throws SQLException {
		return this.st.getString(parameterName);
	}

	@Override
	public boolean getBoolean(String parameterName) throws SQLException {
		return this.st.getBoolean(parameterName);
	}

	@Override
	public byte getByte(String parameterName) throws SQLException {
		return this.st.getByte(parameterName);
	}

	@Override
	public short getShort(String parameterName) throws SQLException {
		return this.st.getShort(parameterName);
	}

	@Override
	public int getInt(String parameterName) throws SQLException {
		return this.st.getInt(parameterName);
	}

	@Override
	public long getLong(String parameterName) throws SQLException {
		return this.st.getLong(parameterName);
	}

	@Override
	public float getFloat(String parameterName) throws SQLException {
		return this.st.getFloat(parameterName);
	}

	@Override
	public double getDouble(String parameterName) throws SQLException {
		return this.st.getDouble(parameterName);
	}

	@Override
	public byte[] getBytes(String parameterName) throws SQLException {
		return this.st.getBytes(parameterName);
	}

	@Override
	public Date getDate(String parameterName) throws SQLException {
		return this.st.getDate(parameterName);
	}

	@Override
	public Time getTime(String parameterName) throws SQLException {
		return this.st.getTime(parameterName);
	}

	@Override
	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return this.st.getTimestamp(parameterName);
	}

	@Override
	public Object getObject(String parameterName) throws SQLException {
		return this.st.getObject(parameterName);
	}

	@Override
	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return this.st.getBigDecimal(parameterName);
	}

	@Override
	public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
		return this.st.getObject(parameterName, map);
	}

	@Override
	public Ref getRef(String parameterName) throws SQLException {
		return this.st.getRef(parameterName);
	}

	@Override
	public Blob getBlob(String parameterName) throws SQLException {
		return this.st.getBlob(parameterName);
	}

	@Override
	public Clob getClob(String parameterName) throws SQLException {
		return this.st.getClob(parameterName);
	}

	@Override
	public Array getArray(String parameterName) throws SQLException {
		return this.st.getArray(parameterName);
	}

	@Override
	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return this.st.getDate(parameterName, cal);
	}

	@Override
	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return this.st.getTime(parameterName, cal);
	}

	@Override
	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
		return this.st.getTimestamp(parameterName, cal);
	}

	@Override
	public URL getURL(String parameterName) throws SQLException {
		return this.st.getURL(parameterName);
	}

	@Override
	public RowId getRowId(int parameterIndex) throws SQLException {
		return this.st.getRowId(parameterIndex);
	}

	@Override
	public RowId getRowId(String parameterName) throws SQLException {
		return this.st.getRowId(parameterName);
	}

	@Override
	public void setRowId(String parameterName, RowId x) throws SQLException {
		this.st.setRowId(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setNString(String parameterName, String value) throws SQLException {
		this.st.setNString(parameterName, value);
		this.callparam.put(parameterName, value);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
		this.st.setNCharacterStream(parameterName, value, length);
		this.callparam.put(parameterName, value);
	}

	@Override
	public void setNClob(String parameterName, NClob value) throws SQLException {
		this.st.setNClob(parameterName, value);
		this.callparam.put(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader, long length) throws SQLException {
		this.st.setClob(parameterName, reader, length);
		this.callparam.put(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
		this.st.setBlob(parameterName, inputStream, length);
		this.callparam.put(parameterName, inputStream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
		this.st.setNClob(parameterName, reader, length);
		this.callparam.put(parameterName, reader);
	}

	@Override
	public NClob getNClob(int parameterIndex) throws SQLException {
		return this.st.getNClob(parameterIndex);
	}

	@Override
	public NClob getNClob(String parameterName) throws SQLException {
		return this.st.getNClob(parameterName);
	}

	@Override
	public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
		this.st.setSQLXML(parameterName, xmlObject);
		this.callparam.put(parameterName, xmlObject);
	}

	@Override
	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return this.st.getSQLXML(parameterIndex);
	}

	@Override
	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return this.st.getSQLXML(parameterName);
	}

	@Override
	public String getNString(int parameterIndex) throws SQLException {
		return this.st.getNString(parameterIndex);
	}

	@Override
	public String getNString(String parameterName) throws SQLException {
		return this.st.getNString(parameterName);
	}

	@Override
	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return this.st.getNCharacterStream(parameterIndex);
	}

	@Override
	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return this.st.getNCharacterStream(parameterName);
	}

	@Override
	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return this.st.getCharacterStream(parameterIndex);
	}

	@Override
	public Reader getCharacterStream(String parameterName) throws SQLException {
		return this.st.getCharacterStream(parameterName);
	}

	@Override
	public void setBlob(String parameterName, Blob x) throws SQLException {
		this.st.setBlob(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setClob(String parameterName, Clob x) throws SQLException {
		this.st.setClob(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
		this.st.setAsciiStream(parameterName, x, length);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
		this.st.setBinaryStream(parameterName, x, length);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
		this.st.setCharacterStream(parameterName, reader, length);
		this.callparam.put(parameterName, reader);
	}

	@Override
	public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
		this.st.setAsciiStream(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
		this.st.setBinaryStream(parameterName, x);
		this.callparam.put(parameterName, x);
	}

	@Override
	public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
		this.st.setCharacterStream(parameterName, reader);
		this.callparam.put(parameterName, reader);
	}

	@Override
	public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
		this.st.setNCharacterStream(parameterName, value);
		this.callparam.put(parameterName, value);
	}

	@Override
	public void setClob(String parameterName, Reader reader) throws SQLException {
		this.st.setClob(parameterName, reader);
		this.callparam.put(parameterName, reader);
	}

	@Override
	public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
		this.st.setBlob(parameterName, inputStream);
		this.callparam.put(parameterName, inputStream);
	}

	@Override
	public void setNClob(String parameterName, Reader reader) throws SQLException {
		this.st.setNClob(parameterName, reader);
		this.callparam.put(parameterName, reader);
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return this.st.getObject(parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return this.st.getObject(parameterName, type);
	}

}
