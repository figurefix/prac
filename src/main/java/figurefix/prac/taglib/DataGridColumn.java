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

public class DataGridColumn {

	private String title = null;
	private String name = null;
	private String style = null;
	private String jsFunction = null;
	private String[] jsArgus = null;
	private String cmpFunction = null;
	
	private static void testStyle(String style) {
		if ( style!=null && (style.contains("'") || style.contains("\""))) {
			throw new RuntimeException("style string should not contains ' or \" characters");
		}
	}
	
	DataGridColumn(String title, String name) {
		this.title = title;
		this.name = name;
		FormGrid.testColName(name);
	}

	DataGridColumn(String title, String name, String style) {
		this.title = title;
		this.name = name;
		this.style = style;
		FormGrid.testColName(name);
		testStyle(style);
	}
	
	public DataGridColumn setJsFunction(String jsfunc) {
		FormGrid.testColName(jsfunc);
		this.jsFunction = jsfunc;
		return this;
	}
	
	public DataGridColumn setJsArgus(String ... names) {
		this.jsArgus = names;
		return this;
	}
	
	public DataGridColumn setCmpFunction(String cmpfunc) {
		FormGrid.testColName(cmpfunc);
		this.cmpFunction = cmpfunc;
		return this;
	}

	String getTitle() {
		return title;
	}

	String getName() {
		return name;
	}

	String getJsFunction() {
		return jsFunction;
	}

	String[] getJsArgus() {
		return jsArgus;
	}

	String getStyle() {
		return style;
	}

	String getCmpFunction() {
		return cmpFunction;
	}
}
