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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import figurefix.prac.logging.SrcLog;
import figurefix.prac.util.DataMap;
import figurefix.prac.util.DataSet;

/**
 * export report based on .xlsx template
 */
public class Report {

	public static final String DS_NAME_FORMAT = "\\{(\\w)+(.(\\w)+)*\\}";
	
	private SheetCase[] sheets = null;
	private int activeSheetIndex = -1;
	private int rowAccessWindowSize = 100;
	private Workbook wb = null;
	
	private Report() {
		
	}
	
	public void close() {
		try {
			this.wb.close();
		} catch (IOException e) {
			SrcLog.error(e);
		}
	}
	
	/**
	 * read template file
	 * @param file .xlsx
	 * @return report object
	 * @throws Exception exp
	 */
	public static Report getInstance(String file) throws Exception {
		Report rpt = new Report();

		OPCPackage pkg;
		try {
			pkg = OPCPackage.open(file, PackageAccess.READ);
		} catch (Exception e) {
			throw new Exception("failed to open '"+file+"', "+e.toString(), e);
		}
		rpt.wb = new XSSFWorkbook(pkg);
		
		int stcnt = rpt.wb.getNumberOfSheets();
		rpt.sheets = new SheetCase[stcnt];
		for(int i=0; i<stcnt; i++) {
			Sheet st = rpt.wb.getSheetAt(i);
			rpt.sheets[i] = new SheetCase(st);
		}
		rpt.activeSheetIndex = rpt.wb.getActiveSheetIndex();
		return rpt;
	}
	
	/**
	 * export report to outputstream
	 * @param data report data
	 * @param os outputstream
	 * @throws Exception exp
	 */
	public synchronized void export(DataMap data, OutputStream os) throws Exception {
		
		boolean sptsteaming = Streaming.isSupported();
		
		XSSFWorkbook xwb = new XSSFWorkbook();
		Workbook wb = sptsteaming ? new SXSSFWorkbook(xwb, this.rowAccessWindowSize) : xwb;
		for(int i=0; i<this.sheets.length; i++) {
			this.sheets[i].build(wb, data);
		}
		wb.setActiveSheet(this.activeSheetIndex);
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
	
	/**
	 * export report to file
	 * @param data report data
	 * @param file report file
	 * @throws Exception exp
	 */
	public void export(DataSet data, String file) throws Exception {
		FileOutputStream os = new FileOutputStream(file);
		this.export(data, os);
	}
	
	/**
	 * export report to http servlet response output stream
	 * @param data report data
	 * @param rsp HttpServletResponse
	 * @param filename file name
	 * @throws Exception exp
	 */
	public void export(DataSet data, HttpServletResponse rsp, String filename) throws Exception {
		rsp.setStatus(HttpServletResponse.SC_OK);
		rsp.setContentType("xlsx:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	//	rsp.setContentLength();
		rsp.addHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(filename, "UTF-8"));
		ServletOutputStream os = rsp.getOutputStream();
		this.export(data, os);
	}

	/**
	 * set active sheet by index
	 * @param activeSheetIndex sheet index from 0
	 */
	public void setActiveSheetIndex(int activeSheetIndex) {
		this.activeSheetIndex = activeSheetIndex;
	}

	/**
	 * set data cache windows size<br>
	 * @param rowAccessWindowSize line count
	 * @see org.apache.poi.xssf.streaming.SXSSFWorkbook
	 */
	public void setRowAccessWindowSize(int rowAccessWindowSize) {
		this.rowAccessWindowSize = rowAccessWindowSize;
	}
}
