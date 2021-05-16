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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import figurefix.prac.util.DataMap;

public class CellCase {

	private Cell cell = null;
	private CellStyle style = null;
	private boolean knowStyle = false;
	
	CellCase(Cell cl) {
		this.cell = cl;
	}
	
	public void build(Row ro, int column, DataMap data, MultiDataLists mlist) throws Exception {
		if(this.cell==null) {
			return;
		}
		String listds = null;
		String dsname = null;
		Object val = null;
		if(this.cell.getCellTypeEnum()==CellType.STRING) {
			String name = this.cell.getStringCellValue();
			if(name.matches(Report.DS_NAME_FORMAT)) {
				if(name.contains(".")) {
					listds = name.substring(1, name.indexOf("."));
					dsname = name.substring(name.indexOf(".")+1, name.length()-1);
					if(mlist!=null) {
						DataMap ds = mlist.get(listds);
						if(ds!=null) {
							val = ds.get(dsname);
						}
					}
				} else {
					dsname = name.substring(1, name.length()-1);
					val = data.get(dsname);
				}
				if(val==null) {
					val = "";
				}
			} else if(name.trim().length()>0) {
				val = name;
			}
		}

		Cell cel = null;
		if(mlist!=null && val==null) {
			// do not create cell for empty list value
		} else if(val!=null) {
			cel = ro.createCell(column);
			if(val instanceof CellData) {
				( (CellData)val ).write(cel);
			} else if(val instanceof Short) {
				cel.setCellValue( (Short)val );
			} else if(val instanceof Integer) {
				cel.setCellValue( (Integer)val );
			} else if(val instanceof Long) {
				cel.setCellValue( (Long)val );
			} else if(val instanceof Float) {
				cel.setCellValue( (Float)val );						
			} else if(val instanceof Double) {
				cel.setCellValue( (Double)val );							
			} else if(val != null) {
				cel.setCellValue( val.toString() );											
			}
		} else {
			cel = ro.createCell(column);
			CellType typ = this.cell.getCellTypeEnum();
			switch(typ) {
			case BOOLEAN:
				cel.setCellValue(this.cell.getBooleanCellValue());
				break;
			case ERROR:
				cel.setCellValue(this.cell.getErrorCellValue());
				break;
			case NUMERIC:
				cel.setCellValue(this.cell.getNumericCellValue());
				break;
			case STRING:
				cel.setCellValue(this.cell.getStringCellValue());
				break;
			default:
				break;
			}	
		}

		if ( ! this.knowStyle ) {
			CellStyle ceStyle = this.cell.getCellStyle();
			CellStyle wbstyle = this.cell.getSheet().getWorkbook().getCellStyleAt((short)0);
			if ( ! ceStyle.equals(wbstyle) ) { // the template cell has a specific style
				this.style = ro.getSheet().getWorkbook().createCellStyle();
				this.style.cloneStyleFrom(ceStyle);	
			}
			this.knowStyle = true;
		}
		if(this.style!=null && cel!=null) {
			cel.setCellStyle(this.style);			
		}
	}
}
