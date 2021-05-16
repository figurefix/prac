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

public class Column {

	private String type = null;
	private String name = null;
	private String title = null;
	private String align = null;
	private boolean wrap = false;
	private int maxlength = -1;
	private String style = null;
	private String options = null;
	private String href = null;
	private StringBuilder evtObjLiteral = null;
	
	String getType() {
		return type;
	}
	
	String getName() {
		return name;
	}
	
	String getTitle() {
		return title;
	}
	
	String getAlign() {
		return align;
	}
	
	boolean isWrap() {
		return wrap;
	}
	
	int getMaxlength() {
		return maxlength;
	}
	
	String getStyle() {
		return style;
	}
	
	String getOptions() {
		return options;
	}
	
	String getHref() {
		return href;
	}
	
	void setType(String type) {
		this.type = type;
	}
	
	void setName(String name) {
		this.name = name;
	}
	
	void setTitle(String title) {
		this.title = title;
	}
	
	void setAlign(String align) {
		this.align = align;
	}
	
	void setWrap(boolean wrap) {
		this.wrap = wrap;
	}
	
	void setMaxlength(int maxlength) {
		this.maxlength = maxlength;
	}
	
	void setStyle(String style) {
		this.style = style;
	}
	
	void setOptions(String options) {
		this.options = options;
	}
	
	void setHref(String href) {
		this.href = href;
	}
	
	void addEvent(String event, String func) {
		if(event==null || !event.toLowerCase().matches("^on[a-z]+")) {
			return;
		}
		if(this.evtObjLiteral == null) {
			this.evtObjLiteral = new StringBuilder("{");
		} else {
			this.evtObjLiteral.append(",");
		}
		this.evtObjLiteral.append("'"+event+"':"+func);
	}
	
	String getEventsObjectLiteral() {
		if(this.evtObjLiteral==null) {
			return null;
		} else {
			return this.evtObjLiteral.toString()+"}";
		}
	}

}
