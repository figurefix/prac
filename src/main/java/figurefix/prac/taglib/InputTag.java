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

import figurefix.prac.taglib.html.AttributeList;
import figurefix.prac.taglib.html.HtmlElement;

/**
 * this tag has the same attribute list of HTML INPUT<br>
 *
 */
public class InputTag extends TagBuilder {
	
	private String type = null;
	private String name = null;

	@Override
	protected String build() throws Exception {
		AttributeList alist = this.getAttributeList();
		alist.set("type", (this.type!=null ? this.type : "text"));
		alist.set("name", this.name);
		String value = this.getValue(this.name);
		if(value!=null) {
			if("checkbox".equals(this.type)) {
				if( this.contains(this.name, "on") || this.contains(this.name, alist.get("value"))) {
					alist.set("checked");					
				}
			} else if("radio".equals(this.type)) {
				if(value.equals(alist.get("value"))) {
					alist.set("checked");
				}
			} else {
				alist.set("value", value);
			}
		}
		if(alist.contains("readonly")) {
			alist.set("readonly");
		}
		if(alist.contains("disabled")) {
			alist.set("disabled");
		}
		HtmlElement html = new HtmlElement("input", alist);
		return html.toString();
	}

	public void setType(String type) {
		this.type = type.trim().toLowerCase();
	}

	public void setName(String name) {
		this.name = name;
	}

}
