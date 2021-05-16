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

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import figurefix.prac.util.DataMap;

public class SheetCase {

	private Sheet sheet = null;
	private RowCase[] rows = null;
	private short lastColumnIdx = 0;
	
	SheetCase(Sheet st) throws Exception {
		this.sheet = st;
		int rownum = st.getLastRowNum();
		this.rows = new RowCase[rownum+1];
		for(int i=0; i<=rownum; i++) {
			Row ro = st.getRow(i);
			this.rows[i] = new RowCase(ro);
			if(ro!=null) {
				short last = ro.getLastCellNum();
				if(last>this.lastColumnIdx) {
					this.lastColumnIdx = last;
				}				
			}
		}
	}

	public void build(Workbook wb, DataMap data) throws Exception {
		if(this.sheet==null) {
			return;
		}
		
		Sheet st =  wb.createSheet(this.sheet.getSheetName());
		
		for(int i=0; i<=this.lastColumnIdx; i++) {
			st.setColumnWidth(i, this.sheet.getColumnWidth(i));
			CellStyle sty = st.getWorkbook().createCellStyle();
			sty.cloneStyleFrom(this.sheet.getColumnStyle(i));
			st.setDefaultColumnStyle(i, sty);
		}
		
		int mrgcnt = this.sheet.getNumMergedRegions();
		ArrayList<CellRangeAddress> ranges = new ArrayList<CellRangeAddress>();
		for(int i=0; i<mrgcnt; i++) {
			ranges.add(this.sheet.getMergedRegion(i));
		}
		
		int rownum = 0;
		for(int i=0; i<this.rows.length; i++) {
			int from = rownum;
			rownum = this.rows[i].build(st, rownum, data);
			this.processMergedRange(st, ranges, from, rownum);
		}
	}
	
	private void processMergedRange(Sheet st, ArrayList<CellRangeAddress> ranges, int rowfrom, int rowto) {
		int expanded = rowto - rowfrom -1;
		for(int i=0; i<ranges.size(); i++) {
			CellRangeAddress adr = ranges.get(i);
			if(expanded>0) {
				if(adr.getFirstRow() > rowfrom) {
					adr.setFirstRow( adr.getFirstRow() + expanded );
				}
				adr.setLastRow( adr.getLastRow() + expanded );
			}
			if(adr.getLastRow() == (rowto-1)) {
				st.addMergedRegion(adr);
				ranges.remove(i);
				i--;
			}
		}
	}
}
