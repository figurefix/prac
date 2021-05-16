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

package figurefix.prac.taglib.html;

import java.util.ArrayList;

public class HtmlElement implements HtmlNode {
	
	private String tagName = null;
	private AttributeList attrlist = null;
	private ArrayList<HtmlNode> subs = new ArrayList<HtmlNode>();

	public HtmlElement(String tagName) {
		this.tagName = tagName.trim().toUpperCase();
		this.attrlist = new AttributeList();
	}
	
	public HtmlElement(String tagName, AttributeList attrlist) {
		this.tagName = tagName.trim().toUpperCase();
		this.attrlist = attrlist==null ? new AttributeList() : attrlist;
	}
	
	public String getTagName() {
		return this.tagName;
	}
	
	public boolean isInstanceOf(String tagname) {
		return this.tagName.equalsIgnoreCase(tagname);
	}
	
	public AttributeList getAttributeList() {
		return this.attrlist;
	}
	
	public void setAttributeList(AttributeList attrlist) {
		this.attrlist = attrlist;
	}
	
	public void setAttribute(String name, String value) {
		this.attrlist.set(name, value);
	}
	
	public void setAttribute(String name) {
		this.attrlist.set(name);
	}
	
	public void addSubNode(HtmlNode node) {
		this.subs.add(node);
	}
	
	public String getStartTag() {
		StringBuilder html = new StringBuilder("<");
		html.append(this.tagName);
		if(!this.isSimpleElement()) {
			html.append(this.attrlist.toString());			
		}
		html.append(this.isEmptyElement()?" />":">");
		return html.toString();
	}
	
	private boolean isSimpleElement() {
		return this.isInstanceOf("br") || this.isInstanceOf("hr");
	}
	
	private boolean isEmptyElement() {
		return this.isSimpleElement() || this.isInstanceOf("input");
	}
	
	public String getEndTag() {
		return "</"+this.tagName+">";
	}
	
	public String getEmptyTag() {
		StringBuilder html = new StringBuilder("<");
		html.append(this.tagName).append(this.attrlist.toString()).append(" />");
		return html.toString();
	}
	
	public String toString() {
		String startag = this.getStartTag();
		if(startag.endsWith("/>")) {
			return startag;
		}
		StringBuilder html = new StringBuilder(startag);
		for(int i=0; i<this.subs.size(); i++) {
			html.append(this.subs.get(i));
		}
		html.append(this.getEndTag());
		return html.toString();	
	}
	
}
