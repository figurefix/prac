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

import java.util.ArrayList;

public class Table {

	private String name = null;
	private String alias = null;
	private ArrayList<Column> cols = new ArrayList<Column>();
	
	/**
	 * constructor
	 * @param name table name
	 */
	public Table(String name) {
		this.name = name;
		this.alias = name;
	}

	private final Column addColumn(String name, boolean isNum, boolean pk) {
		Column col = new Column(this, name, isNum, pk);
		cols.add(col);
		return col;
	}
	
	/**
	 * add primary key column
	 * @param name column name
	 * @param coltype {@link figurefix.prac.sql.Column#NUMBER} or {@link figurefix.prac.sql.Column#QUOTED} 
	 * @return column
	 */
	public final Column addPK(String name, int coltype) {
		return this.addColumn(name, coltype==Column.NUMBER, true);
	}

	/**
	 * add non primary key column
	 * @param name column name
	 * @param coltype {@link figurefix.prac.sql.Column#NUMBER} or {@link figurefix.prac.sql.Column#QUOTED} 
	 * @return column
	 */
	public final Column addColumn(String name, int coltype) {
		return this.addColumn(name, coltype==Column.NUMBER, false);
	}

	public final Column getColumn(String name) {
		for(int i=0; i<cols.size(); i++) {
			Column c = cols.get(i);
			if(c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public final Column[] getAllColumns() {
		return cols.toArray(new Column[cols.size()]);
	}

	public final boolean contains(String colname) {
		for(int i=0; i<this.cols.size(); i++) {
			if( this.cols.get(i).getName().equals(colname) ) {
				return true;
			}
		}
		return false;
	}

	public final boolean isPK(String colname) {
		Column col = this.getColumn(colname);
		return col!=null && col.isPK();
	}

	public final String getName() {
		return this.name;
	}

	public final String toString() {
		return this.name;
	}

	public String getAlias() {
		return alias;
	}

	/**
	 * set sql alias for table 
	 * @param alias set null to remove alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
}
