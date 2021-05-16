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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import figurefix.prac.util.Const;
import figurefix.prac.util.DataMap;
import figurefix.prac.util.DataSet;

/**
 * make sqls<br>
 * data names in DataMap could be: columnName or tableName.columnName<br>
 * generate <code>columnName=value</code> by default.<br>
 * columnName ends with the following suffix corresponds to specific sql sign
 * <table border=1 cellspacing=0 cellpadding=2 summary="后缀">
 * <tr><td>&nbsp; <code>|gt</code> &nbsp;</td><td>greater than</td><td>&gt;</td></tr>
 * <tr><td>&nbsp; <code>|lt</code> &nbsp;</td><td>less than</td><td>&lt;</td></tr>
 * <tr><td>&nbsp; <code>|ge</code> &nbsp;</td><td>greater or equal</td><td>&gt;=</td></tr>
 * <tr><td>&nbsp; <code>|le</code> &nbsp;</td><td>less or equal</td><td>&lt;=</td></tr>
 * <tr><td>&nbsp; <code>|ne</code> &nbsp;</td><td>not equal</td><td>!=</td></tr>
 * <tr><td>&nbsp; <code>|lg</code> &nbsp;</td><td>not equal</td><td>&lt;&gt;</td></tr>
 * <tr><td>&nbsp; <code>|in</code> &nbsp;</td><td>in</td><td>in</td></tr>
 * <tr><td>&nbsp; <code>|ni</code> &nbsp;</td><td>not in</td><td>not in</td></tr>
 * <tr><td>&nbsp; <code>|li</code> &nbsp;</td><td>like</td><td>like</td></tr>
 * <tr><td>&nbsp; <code>|il</code> &nbsp;</td><td>not like</td><td>not like</td></tr>
 * </table>
 */
public class Sqler {
	
	private static class NamVal {
		String colname = null;
		String valuestr = null;
		String operator = null;
	}

	private static String getOperator(String name) {
		if(name==null) {
			return "";
		} else if(name.endsWith("|gt")) {
			return ">";
		} else if(name.endsWith("|lt")) {
			return "<";
		} else if(name.endsWith("|ge")) {
			return ">=";
		} else if(name.endsWith("|le")) {
			return ">=";
		} else if(name.endsWith("|ne")) {
			return "!=";
		} else if(name.endsWith("|lg")) {
			return "<>";
		} else if(name.endsWith("|in")) {
			return " in ";
		} else if(name.endsWith("|ni")) {
			return " not in ";
		} else if(name.endsWith("|li")) {
			return " like ";
		} else if(name.endsWith("|il")) {
			return " not like ";
		} else {
			return "=";
		}
	}
	
	private static String formatString(String val) {
		//escape single quotes
		return val==null ? " NULL " : "'"+val.replaceAll("'", "''")+"'";
	}
	
	private static String formatNumber(String val) {
		//reserve only digits and dot
		return val==null ? "0" : val.replaceAll("[^\\d\\.]", "");
	}
	
	private static String getFieldName(String dn) {
		return dn.contains("|") ? dn.substring(0, dn.lastIndexOf("|")) : dn;
	}
	
	private static ArrayList<NamVal> match(Table tb, DataMap data, boolean matchTableAlias) {
		ArrayList<NamVal> nvlist = new ArrayList<NamVal>();
		if(tb==null || data==null) {
			return nvlist;
		}
		String prefix = ( !matchTableAlias 
						|| tb.getAlias()==null 
						|| tb.getAlias().trim().length()==0
						) ? "" : tb.getAlias()+".";
		String[] dnms = data.getNames();
		Column[] cols = tb.getAllColumns();
		for(int i=0; i<dnms.length; i++) {
			String dn = dnms[i];
			String name = getFieldName(dn);
			for(int j=0; j<cols.length; j++) {
				Column col = cols[j];
				String field = prefix + col.getName();
				if( name.equals(field) ) {
					NamVal nv = new NamVal();
					nv.colname = col.getName();
					if(data.get(dn)==null) {
						nv.valuestr = "NULL";
					} else {
						String value = data.get(dn).toString();
						if(col.isNumber()) {
							nv.valuestr = formatNumber(value.toString()); 
						} else {
							nv.valuestr = formatString(value);	
						}
					}
					nv.operator = getOperator(dn);
					nvlist.add(nv);
					break;
				}
			}
		}
		return nvlist;
	}
	
	public static String getSetClause(DataMap data, Table ... tb) {
		return make(SqlerType.SET, data, tb);
	}
	
	public static String getOrClause(DataMap data, Table ... tb) {
		return make(SqlerType.OR, data, tb);
	}

	public static String getAndClause(DataMap data, Table ... tb) {
		return make(SqlerType.AND, data, tb);
	}
	
	private static String make(SqlerType typ, DataMap data, Table[] tb) {
		if(typ==null || data==null || tb==null || tb.length==0) {
			return "";
		}
		StringBuilder sql = new StringBuilder(" ");
		boolean sep = false;
		for(int t=0; t<tb.length; t++) {
			ArrayList<NamVal> nvlist = match(tb[t], data, true);
			for(int i=0; i<nvlist.size(); i++) {
				NamVal nv = nvlist.get(i);
				if(sep) {
					sql.append(typ.getSeparator());
				} else {
					sep = true;
					if(typ==SqlerType.SET) {
						sql.append("set ");
					}
				}
				if( typ==SqlerType.AND || typ==SqlerType.OR ) {
					sql.append(tb[t].getAlias()).append(".");
				}
				sql.append(nv.colname);
				if(typ==SqlerType.SET) {
					sql.append("=").append(nv.valuestr);
				} else {
					sql.append(nv.operator);
					if(nv.operator.contains("in")) {
						sql.append("(").append(nv.valuestr).append(")");
					} else {
						sql.append(nv.valuestr);
					}
				}
			}			
		}
		sql.append(" ");
		return sql.toString();
	}
	
	public static String getInsertStatement(Table tb, DataMap data, boolean matchTableAlias) {
		
		ArrayList<NamVal> nvlist = match(tb, data, matchTableAlias);
		
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(tb.getName()).append(" (");
		StringBuilder val = new StringBuilder(" values (");
		for(int i=0; i<nvlist.size(); i++) {
			if(i!=0) {
				sql.append(", ");
				val.append(", ");
			}
			NamVal nv = nvlist.get(i);
			sql.append(nv.colname);
			val.append(nv.valuestr);
		}
		sql.append(")");
		val.append(")");

		return sql.append(val).toString();
	}

	public static String getUpdateStatement(Table tb, DataMap data, boolean matchTableAlias) {
		ArrayList<NamVal> nvlist = match(tb, data, matchTableAlias);
		StringBuilder sql = new StringBuilder("update ");
		sql.append(tb.getName()).append(" set ");
		StringBuilder where = new StringBuilder(" where ");
		boolean comma = false;
		boolean and = false;
		for(int i=0; i<nvlist.size(); i++) {
			NamVal nv = nvlist.get(i);
			if(tb.isPK(nv.colname)) {
				if(and) {
					where.append(" and ");
				} else {
					and = true;
				}
				where.append(nv.colname).append("=").append(nv.valuestr);
			} else {
				if(comma) {
					sql.append(", ");
				} else {
					comma = true;
				}
				sql.append(nv.colname).append("=").append(nv.valuestr);
			}
		}
		sql.append(where);
		return sql.toString();
	}

	public static String getInsertStatement(String table, DataMap values) {
		StringBuilder sql = new StringBuilder("insert into ");
		sql.append(table).append(" (");
		StringBuilder value = new StringBuilder(" values (");
		String[] names = values.getNames();
		for(int i=0; i<names.length; i++) {
			String name = names[i];
			if(i!=0) {
				sql.append(", ");
				value.append(", ");
			}
			sql.append(name);
			value.append(getSqlLiteral(values, name));
		}
		sql.append(")").append(value).append(")");
		
		return sql.toString();
	}

	public static String getUpdateStatement(String table, DataSet values) {
		StringBuilder sql = new StringBuilder("update ");
		sql.append(table).append(" set ");
		boolean comma = false;
		StringBuilder where = new StringBuilder(" where ");
		boolean and = false;
		String[] names = values.getNames();
		for(int i=0; i<names.length; i++) {
			String name = names[i];
			if(values.isKey(name)) {
				if(and) {
					where.append(" and ");
				} else {
					and = true;
				}
				where.append(name).append("=").append(getSqlLiteral(values, name));
			} else {
				if(comma) {
					sql.append(", ");
				} else {
					comma = true;
				}
				sql.append(name).append("=").append(getSqlLiteral(values, name));
			}
		}
		sql.append(where);
		return sql.toString();
	}
	
	public static String getSqlLiteral(DataMap map, String name) {
		Object o = map.get(name);
		if(o==null) {
			return " NULL ";
		}
		if(o instanceof Const) {
			Const cc = (Const)o;
			if(cc.isInt()) {
				return ""+cc.intValue();
			} else {
				return formatString(cc.toString());
			}
		} else if(o instanceof Byte) {
			return ""+((Byte)o).byteValue();
		} else if(o instanceof Integer) {
			return ""+((Integer)o).intValue();
		} else if(o instanceof Long) {
			return ""+((Long)o).longValue();
		} else if(o instanceof Float) {
			return ""+((Float)o).floatValue();
		} else if(o instanceof Double) {
			return ""+((Double)o).doubleValue();
		} else if(o instanceof BigInteger) {
			return ((BigInteger)o).toString();
		} else if(o instanceof BigDecimal) {
			return ((BigDecimal)o).toString();
		} else if(o instanceof Character) {
			return formatString(""+(((Character)o).charValue()));
		} else if(o instanceof String) {
			return formatString(((String)o));
		} else {
			return formatString(o.toString());
		}
	}
}
