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

package figurefix.prac.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class DataSet implements DataMap {

	private HashMap<String, Object> vmap = new HashMap<String, Object>();
	private ArrayList<String> keys = new ArrayList<String>();
	
	public DataSet() {
		
	}
	
	/**
	 * build data set by reading parameters and attributes from request
	 * @param req request
	 */
	public DataSet(HttpServletRequest req) {
		this(req, true, true);
	}
	
	/**
	 * build data set by reading parameters and attributes from request
	 * @param req request
	 * @param incPara whether includes parameters
	 * @param incAttr whether includes attributes
	 */
	public DataSet(HttpServletRequest req, boolean incPara, boolean incAttr) {
		
		Enumeration<String> en = null;
		if(incPara) {
			en = req.getParameterNames();
			while(en.hasMoreElements()) {
				String name = en.nextElement();
				this.set(name, req.getParameter(name));
			}			
		}
		
		if(incAttr) {
			en = req.getAttributeNames();
			while(en.hasMoreElements()) {
				String name = en.nextElement();
				Object attr = req.getAttribute(name);
				if( attr != null ) {
					this.set(name, attr);	
				}
			}			
		}
	}
	
	/**
	 * construct DataSet by fetching data from the current row of ResultSet
	 * @param rs result set
	 * @throws SQLException exception
	 */
	public DataSet(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		for(int i=1; i<=cnt; i++) {
			String name = meta.getColumnName(i);
			this.set(name, rs.getObject(i));
		}
	}
	
	/**
	 * put all data rows in the result set into the given list
	 * @param list the list to fill
	 * @param rs result set
	 * @throws SQLException exception
	 */
	public static void list(List<DataSet> list, ResultSet rs) throws SQLException {
		list(list, rs, 1, 0);
	}
	
	/**
	 * read result set into a list
	 * @param list the list to fill
	 * @param rs result set
	 * @param from data rows in the result set will be read from this index. 
	 *        it starts from 1
	 * @param count the maximum data rows to be read from the result set. 
	 *        this parameter will be ignored when it's less than or equal to zero.
	 *        actual quantity of data rows read from the result set may less than this count.
	 * @return true: there are more data in the result set to be read, false: otherwise
	 * @throws SQLException exception
	 */
	public static boolean list(List<DataSet> list, ResultSet rs, int from , int count) throws SQLException {
		if(list==null) {
			throw new IllegalArgumentException("List<DataSet> null");
		}
		ResultSetMetaData meta = rs.getMetaData();
		int cnt = meta.getColumnCount();
		while(rs.next()) {
			if((--from)>0) {
				continue;
			}
			DataSet ds = new DataSet();
			for(int i=1; i<=cnt; i++) {
				String name = meta.getColumnName(i);
				ds.set(name, rs.getObject(i));
			}
			list.add(ds);
			if(count>0 && (--count)<=0) {
				return rs.next();
			}
		}
		return false;
	}
	
	public void toAttributes(HttpServletRequest req) {
		String[] names = this.getNames();
		for(int i=0; i<names.length; i++) {
			req.setAttribute(names[i], this.get(names[i]));
		}
	}
	
	@Override
	public boolean contains(String name) {
		return this.vmap.containsKey(name);
	}

	@Override
	public boolean containsValue(String name) {
		return this.vmap.get(name)!=null;
	}

	public boolean isKey(String name) {
		return this.keys.contains(name);
	}

	@Override
	public synchronized void set(String name, Object value) {
		if(name==null) {
			return;
		}
		this.vmap.put(name, value);
	}

	public synchronized void setKey(String name, Object value) {
		if(name==null) {
			return;
		}
		this.vmap.put(name, value);
		this.keys.add(name);
	}

	@Override
	public synchronized void remove(String name) {
		this.vmap.remove(name);
		this.keys.remove(name);
	}
	
	public synchronized void removeNullValues() {
		Set<String> ks = this.vmap.keySet();
		String[] key = ks.toArray(new String[ks.size()]);
		for(int i=0; i<key.length; i++) {
			if(this.vmap.get(key[i])==null) {
				this.vmap.remove(key[i]);
				this.keys.remove(key[i]);
			}
		}
	}
	
	public synchronized void removeEmptyStrings() {
		Set<String> ks = this.vmap.keySet();
		String[] key = ks.toArray(new String[ks.size()]);
		for(int i=0; i<key.length; i++) {
			Object val = this.vmap.get(key[i]);
			if(val instanceof String) {
				if(((String)val).trim().length()==0) {
					this.vmap.remove(key[i]);
					this.keys.remove(key[i]);					
				}
			}
		}
	}
	
	@Override
	public String[] getNames() {
		Set<String> keys = this.vmap.keySet();
		return keys.toArray(new String[keys.size()]);
	}

	@Override
	public Object get(String name) {
		return this.vmap.get(name);
	}
	
	/**
	 * convert the value to be compatible to {@code java.sql.PreparedStatement#setObject(int, Object)}
	 * @param name attribute name
	 * @return converted value
	 */
	public Object getSqlObj(String name) {
		Object o = this.vmap.get(name);
		if(o!=null && o instanceof Const) {
			Const c = (Const)o;
			if(c.isInt()) {
				return c.intValue();
			} else {
				return c.toString();
			}
		} else {
			return o;
		}
	}
	
	/**
	 * copy the specified attributes to a new dataset
	 * @param names attribute names
	 * @return a new dataset
	 */
	public DataSet copy(String ... names) {
		DataSet ds = new DataSet();
		if(names==null) {
			return ds;
		}
		for(int i=0; i<names.length; i++) {
			String nm = names[i];
			if(this.isKey(nm)) {
				ds.setKey(nm, this.get(nm));
			} else {
				ds.set(nm, this.get(nm));				
			}
		}
		return ds;
	}
	
	public DataSet copy() {
		DataSet ds = new DataSet();
		ds.vmap.putAll(this.vmap);
		ds.keys.addAll(this.keys);
		return ds;
	}

	public String getString(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof String) {
			return (String)o;
		} else if(o!=null) {
			return o.toString();
		}
		return null;
	}

	public char getChar(String name) {
		Character val = this.getCharacter(name);
		return val!=null ? val : (char)0;
	}
	
	public Character getCharacter(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof Character) {
			return (Character)o;
		} else if(o instanceof String && ((String)o).length()>0) {
			return ( (String)o ).charAt(0);
		}
		return null;
	}

	public byte getB(String name) {
		Byte val = this.getByte(name);
		return val!=null ? val : (byte)0;
	}
	
	public Byte getByte(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof Byte) {
			return ( (Byte)o ).byteValue();
		} else if(o instanceof Integer) {
			return ( (Integer)o ).byteValue();
		} else if(o instanceof Long) {
			return ( (Long)o ).byteValue();
		} else if(o instanceof Float) {
			return ( (Float)o ).byteValue();
		} else if(o instanceof Double) {
			return ( (Double)o ).byteValue();
		} else if(o instanceof BigInteger) {
			return ( (BigInteger)o ).byteValue();
		} else if(o instanceof BigDecimal) {
			return ( (BigDecimal)o ).byteValue();
		}
		return null;
	}
	
	public int getInt(String name) {
		Integer val = this.getInteger(name);
		return val!=null ? val : 0;
	}
	
	public Integer getInteger(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof Byte) {
			return ( (Byte)o ).intValue();
		} else if(o instanceof Integer) {
			return ( (Integer)o ).intValue();
		} else if(o instanceof Long) {
			return ( (Long)o ).intValue();
		} else if(o instanceof Float) {
			return ( (Float)o ).intValue();
		} else if(o instanceof Double) {
			return ( (Double)o ).intValue();
		} else if(o instanceof BigInteger) {
			return ( (BigInteger)o ).intValue();
		} else if(o instanceof BigDecimal) {
			return ( (BigDecimal)o ).intValue();
		}
		return null;
	}

	public long getL(String name) {
		Long val = this.getLong(name);
		return val!=null ? val : 0;
	}
	
	public Long getLong(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof Byte) {
			return ( (Byte)o ).longValue();
		} else if(o instanceof Integer) {
			return ( (Integer)o ).longValue();
		} else if(o instanceof Long) {
			return ( (Long)o ).longValue();
		} else if(o instanceof Float) {
			return ( (Float)o ).longValue();
		} else if(o instanceof Double) {
			return ( (Double)o ).longValue();
		} else if(o instanceof BigInteger) {
			return ( (BigInteger)o ).longValue();
		} else if(o instanceof BigDecimal) {
			return ( (BigDecimal)o ).longValue();
		}
		return null;
	}
	
	public float getF(String name) {
		Float val = this.getFloat(name);
		return val!=null ? val : 0;
	}
	
	public Float getFloat(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof Byte) {
			return ( (Byte)o ).floatValue();
		} else if(o instanceof Integer) {
			return ( (Integer)o ).floatValue();
		} else if(o instanceof Long) {
			return ( (Long)o ).floatValue();
		} else if(o instanceof Float) {
			return ( (Float)o ).floatValue();
		} else if(o instanceof Double) {
			return ( (Double)o ).floatValue();
		} else if(o instanceof BigInteger) {
			return ( (BigInteger)o ).floatValue();
		} else if(o instanceof BigDecimal) {
			return ( (BigDecimal)o ).floatValue();
		}
		return null;
	}
	
	public double getD(String name) {
		Double val = this.getDouble(name);
		return val!=null ? val : 0;
	}
	
	public Double getDouble(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof Byte) {
			return ( (Byte)o ).doubleValue();
		} else if(o instanceof Integer) {
			return ( (Integer)o ).doubleValue();
		} else if(o instanceof Long) {
			return ( (Long)o ).doubleValue();
		} else if(o instanceof Float) {
			return ( (Float)o ).doubleValue();
		} else if(o instanceof Double) {
			return ( (Double)o ).doubleValue();
		} else if(o instanceof BigInteger) {
			return ( (BigInteger)o ).doubleValue();
		} else if(o instanceof BigDecimal) {
			return ( (BigDecimal)o ).doubleValue();
		}
		return null;
	}

	public BigInteger getBigInteger(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof BigInteger) {
			return (BigInteger)o;
		} else if(o instanceof BigDecimal) {
			return ( (BigDecimal)o ).toBigInteger();
		} else if(o instanceof Byte) {
			return BigInteger.valueOf( ( (Byte)o ).longValue() );
		} else if(o instanceof Integer) {
			return BigInteger.valueOf( ( (Integer)o ).longValue() );
		} else if(o instanceof Long) {
			return BigInteger.valueOf( ( (Long)o ).longValue() );
		} else if(o instanceof Float) {
			return BigInteger.valueOf( ( (Float)o ).longValue() );
		} else if(o instanceof Double) {
			return BigInteger.valueOf( ( (Double)o ).longValue() );
		}
		return null;
	}

	public BigDecimal getBigDecimal(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof BigDecimal) {
			return (BigDecimal)o;
		} else if(o instanceof BigInteger) {
			return new BigDecimal((BigInteger)o, 0);
		} else if(o instanceof Byte) {
			return BigDecimal.valueOf( ( (Byte)o ).doubleValue() );
		} else if(o instanceof Integer) {
			return BigDecimal.valueOf( ( (Integer)o ).doubleValue() );
		} else if(o instanceof Long) {
			return BigDecimal.valueOf( ( (Long)o ).doubleValue() );
		} else if(o instanceof Float) {
			return BigDecimal.valueOf( ( (Float)o ).doubleValue() );
		} else if(o instanceof Double) {
			return BigDecimal.valueOf( ( (Double)o ).doubleValue() );
		}
		return null;
	}
	
	public AnyDecimal getAnyDecimal(String name) {
		Object o = this.vmap.get(name);
		return new AnyDecimal(o);
	}
	
	public Timestamp getTimestamp(String name) {
		Object o = this.vmap.get(name);
		if(o instanceof Timestamp) {
			return (Timestamp)o;
		} else {
			try {
				Timestamp.valueOf(o.toString());
			} catch (Exception e) {
				
			}
		}
		return null;
	}
}
