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

import java.util.Iterator;

import figurefix.prac.util.DataMap;

public class MultiDataLists {

	private String sheetName = null;
	private DataMap ds = null;
	private String[] listDsNames = null;
	private DataMap[] currentDS = null;
	private int index = -1;
	
	MultiDataLists(String stName, DataMap ds, String[] listDsNames) {
		this.sheetName = stName;
		this.ds = ds;
		this.listDsNames = listDsNames;
		this.currentDS = new DataMap[listDsNames.length];	
	}
	
	boolean next() throws Exception {
		this.index++;
		boolean hasNext = false;
		for (int i=0; i<this.listDsNames.length; i++) {
			this.currentDS[i] = null;
			String dsname = this.listDsNames[i];
			Object oo = this.ds.get(dsname);
			if ( oo instanceof DataMap[] ) {
				DataMap[] list = (DataMap[])oo;
				if ( this.index < list.length ) {
					this.currentDS[i] = list[this.index];
					hasNext = true;
				}
			} else if ( oo instanceof Iterator<?> ) {
				Iterator<?> list = (Iterator<?>)oo;
				if ( list.hasNext() ) {
					Object dso = list.next();
					if ( dso instanceof DataMap ) {
						this.currentDS[i] = (DataMap)dso;
						hasNext = true;
					} else {
						exp(dsname);
					}
				}
			} else if(oo!=null) { // over look null
				exp(dsname);
			}
		}
		return hasNext;
	}
	
	private void exp(String dsname) throws Exception {
		throw new Exception("sheet["+this.sheetName+"] list data source{"+dsname+".} should be type of [" 
				+ DataMap.class.getName()+"] or [" 
				+ Iterator.class.getName()+"<"+DataMap.class.getName()+">]");		
	}
	
	DataMap get(String listDsName) {
		if(listDsName==null) {
			return null;
		}
		for(int i=0; i<this.listDsNames.length; i++) {
			if(listDsName.equals(this.listDsNames[i])) {
				return this.currentDS[i];
			}
		}
		return null;
	}
}
