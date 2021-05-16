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

package figurefix.prac.report;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import figurefix.prac.util.DataMap;

public class RowCase {
	
	private Row row = null;
	private CellCase[] cells = null;
	private String[] listDsNames = null;

	RowCase(Row ro) throws Exception {
		this.row = ro;
		if(ro==null) {
			return;
		}
		int rownum = ro.getLastCellNum();
		this.cells = new CellCase[rownum+1];
		ArrayList<String> dsnlist = new ArrayList<String>();
		for(int i=0; i<=rownum; i++) {
			Cell cel = ro.getCell(i);
			this.cells[i] = new CellCase(cel);
			if(cel!=null && cel.getCellTypeEnum()==CellType.STRING) {
				String val = cel.getStringCellValue();
				if(val!=null && val.matches(Report.DS_NAME_FORMAT) && val.contains(".")) {
					String listds = val.substring(1, val.indexOf("."));
					if(!dsnlist.contains(listds)) {
						dsnlist.add(listds);
					}
				}
			}
		}
		this.listDsNames = dsnlist.toArray(new String[dsnlist.size()]);
	}
	
	public int build(Sheet st, int rownum, DataMap data) throws Exception {
		if(this.row==null) {
			st.createRow(rownum++);
			return rownum;
		}
		
		MultiDataLists mlist = new MultiDataLists(st.getSheetName(), data, this.listDsNames);
		boolean hasNext = mlist.next();
		
		if(this.listDsNames.length==0 || ! hasNext) {
			Row ro = this.createRow(st, rownum++);
			for(int i=0; i<this.cells.length; i++) {
				this.cells[i].build(ro, i, data, null);
			}
		} else {
			do {
				Row ro = this.createRow(st, rownum++);
				for(int i=0; i<this.cells.length; i++) {
					this.cells[i].build(ro, i, data, mlist);
				}		
			} while (mlist.next());
		}
		return rownum;
	}
	
	private Row createRow(Sheet st, int rownum) throws Exception {
		Row ro = st.createRow(rownum);
		ro.setHeight(this.row.getHeight());
		ro.setRowStyle(this.row.getRowStyle());
		return ro;
	}
}
