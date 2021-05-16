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

package figurefix.prac.taglib;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import figurefix.prac.report.Streaming;
import figurefix.prac.util.DataMap;

public class DataGrid {

	private boolean order = true;
	private boolean fixTitle = true;
	private String fontName = "Arial";
	
	private ArrayList<DataGridColumn> columns = new ArrayList<DataGridColumn>();
	private Iterator<DataMap> data = null;
	
	public DataGrid() {

	}
	
	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public DataGridColumn addColumn(String title, String name) {
		DataGridColumn col = new DataGridColumn(title, name);
		this.columns.add(col);
		return col;
	}
	
	public DataGridColumn addColumn(String title, String name, String style) {
		DataGridColumn col = new DataGridColumn(title, name, style);
		this.columns.add(col);
		return col;
	}
	
	public ArrayList<DataGridColumn> getColumns() {
		return this.columns;
	}
	
	public Iterator<DataMap> getData() {
		return this.data;
	}
	
	public void setData(Iterator<DataMap> data) {
		this.data = data;
	}

	public boolean isOrder() {
		return order;
	}

	public void setOrder(boolean order) {
		this.order = order;
	}

	public boolean isFixTitle() {
		return fixTitle;
	}

	public void setFixTitle(boolean fixTitle) {
		this.fixTitle = fixTitle;
	}
	
	/**
	 * exprot to file
	 * @param rsp HttpServletResponse
	 * @param title title
	 * @throws Exception exp
	 */
	public void export(HttpServletResponse rsp, String title) throws Exception {
		this.export(rsp, title, null, true);
	}
	
	
	public void exportETX(HttpServletResponse rsp, String title) throws Exception {
		this.export(rsp, title, null, false);
	}
	
	public void export(HttpServletResponse rsp, String title, String subtitle) throws Exception {
		this.export(rsp, title, subtitle, true);
	}
	
	public void exportETX(HttpServletResponse rsp, String title, String subtitle) throws Exception {
		this.export(rsp, title, subtitle, false);
	}
	
	/**
	 * export to file
	 * @param rsp HttpServletResponse
	 * @param title title
	 * @param subtitle sub title
	 * @param msOffice true: export MS OFFICE xlsx, false: export WPS OFFICE etx
	 * @throws Exception exp
	 */
	private void export(HttpServletResponse rsp, String title, String subtitle, boolean msOffice) throws Exception {

		boolean sptsteaming = Streaming.isSupported();

		XSSFWorkbook xwb = new XSSFWorkbook();
		Workbook wb = sptsteaming ? (new SXSSFWorkbook(xwb, 100)) : xwb;
		Sheet st = wb.createSheet(title);
		
		boolean hasSubTitle = (subtitle!=null && subtitle.trim().length()>0);
		
		st.createFreezePane(0, hasSubTitle ? 3 : 2);
		
		int rowidx = 0;
		
		Row rTitle = st.createRow(rowidx++);
		rTitle.setHeightInPoints(35);
		Cell cTitle = rTitle.createCell(0);
		cTitle.setCellValue(title);
		CellStyle titleStyle = xwb.createCellStyle();
		Font titleFont = xwb.createFont();
		titleFont.setFontHeightInPoints((short)20);
		titleFont.setFontName(this.fontName);
		titleStyle.setFont(titleFont);
		titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cTitle.setCellStyle(titleStyle);
		st.addMergedRegion(new CellRangeAddress(0, 0, 0, this.columns.size()-1));
		
		if(subtitle!=null && subtitle.trim().length()>0) {
		
			Font subTitleFont = xwb.createFont();
			subTitleFont.setFontName(this.fontName);
			subTitleFont.setFontHeightInPoints((short)14);
			CellStyle subTitleStyle = xwb.createCellStyle();
			subTitleStyle.setFont(subTitleFont);
			subTitleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			
			Row rSubTitle = st.createRow(rowidx++);
			rSubTitle.setHeightInPoints(25);
			Cell cSubTitle = rSubTitle.createCell(0);
			cSubTitle.setCellValue(subtitle);
			cSubTitle.setCellStyle(subTitleStyle);
			st.addMergedRegion(new CellRangeAddress(1, 1, 0, this.columns.size()-1));
		}
		
		XSSFFont ftt = xwb.createFont();
		ftt.setFontName(this.fontName);
		ftt.setColor(new XSSFColor(new java.awt.Color(238, 238, 238)));
		ftt.setFontHeightInPoints((short)16);
		XSSFCellStyle tstyle = xwb.createCellStyle();
		tstyle.setAlignment(HorizontalAlignment.CENTER);
		tstyle.setVerticalAlignment(VerticalAlignment.CENTER);
		tstyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 136, 68)));
		tstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		tstyle.setBorderBottom(BorderStyle.THIN);
		tstyle.setBorderTop(BorderStyle.THIN);
		tstyle.setBorderLeft(BorderStyle.THIN);
		tstyle.setBorderRight(BorderStyle.THIN);
		tstyle.setFont(ftt);

		Row r1 = st.createRow(rowidx++);
		r1.setHeightInPoints((short) 25);

		for (int i = 0; i < this.columns.size(); i++) {
			DataGridColumn col = this.columns.get(i);
			Cell r1c = r1.createCell(i);
			r1c.setCellValue(col.getTitle());
			r1c.setCellStyle(tstyle);
		}
		
		XSSFFont vfont = xwb.createFont();
		vfont.setFontName(this.fontName);
		vfont.setFontHeightInPoints((short)14);
		XSSFCellStyle vstyle = xwb.createCellStyle();
		vstyle.setFont(vfont);
		vstyle.setVerticalAlignment(VerticalAlignment.CENTER);
		vstyle.setBorderBottom(BorderStyle.THIN);
		vstyle.setBorderTop(BorderStyle.THIN);
		vstyle.setBorderLeft(BorderStyle.THIN);
		vstyle.setBorderRight(BorderStyle.THIN);
		
		if (this.data != null) {
			while (this.data.hasNext()) {
				DataMap dm = this.data.next();
				if(dm==null) {
					continue;
				}
				
				Row rr = st.createRow(rowidx++);
				rr.setHeightInPoints((float)25);
				
				for (int i = 0; i < this.columns.size(); i++) {
					DataGridColumn col = this.columns.get(i);
					Object val = dm.get(col.getName());
					if(val==null) {
						continue;
					}
					Cell cc = rr.createCell(i);
					cc.setCellStyle(vstyle);
					if(val instanceof String) {
						cc.setCellValue( (String)val );
					} else if( val instanceof Integer 
							|| val instanceof Long 
							|| val instanceof Float 
							|| val instanceof Double) {
						double dval = Double.parseDouble(val.toString());
						cc.setCellValue(dval);
					} else {
						cc.setCellValue(val.toString());
					}
				}
			}
		}
		
		if(st instanceof SXSSFSheet) {
			((SXSSFSheet)st).trackAllColumnsForAutoSizing();
		}
		for (int i = 0; i < this.columns.size(); i++) {
			st.autoSizeColumn(i);
		}
		
		rsp.setStatus(HttpServletResponse.SC_OK);
		rsp.setContentType("xlsx:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		rsp.addHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(title+(msOffice ? ".xlsx" : ".etx"), "UTF-8"));
		ServletOutputStream os = rsp.getOutputStream();
		wb.write(os);

		if(sptsteaming) {
			((SXSSFWorkbook)wb).dispose();			
		}

		try {
			wb.close();	// no such method in old version POI
		} catch (Throwable e) {

		}
		
		os.flush();
		os.close();
	}
}
