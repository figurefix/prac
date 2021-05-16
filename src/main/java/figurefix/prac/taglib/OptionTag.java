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

import java.io.StringWriter;

import javax.servlet.jsp.tagext.JspTag;

import figurefix.prac.taglib.html.HtmlElement;
import figurefix.prac.taglib.html.TextNode;

/**
 * a tag implementable of HTML OPTION
 *
 */
public class OptionTag extends TagBuilder {

	@Override
	protected String build() throws Exception {
		HtmlElement html = new HtmlElement("option", this.getAttributeList());
		JspTag ptag = this.getParent();
		if(ptag instanceof OptGroupTag) {
			JspTag ppta = ((OptGroupTag)ptag).getParent();
			if(ppta instanceof SelectTag) {
				this.setSelection((SelectTag)ppta);
			}
		} else if(ptag instanceof SelectTag) {
			this.setSelection((SelectTag)ptag);
		} else {
			return null;
		}
		StringWriter sw = new StringWriter();
		if(this.getJspBody()!=null) {
			this.getJspBody().invoke(sw);			
		}
		String body = sw.toString();
		TextNode content = new TextNode(body);
		html.addSubNode(content);
		return html.toString();
	}
	
	private void setSelection(SelectTag sel) {
		String val = sel.getValue();
		if(val!=null) {
			if( val.equals( this.getAttributeList().get("value") ) ) {
				this.getAttributeList().set("selected");				
			} else {
				this.getAttributeList().remove("selected");
			}
		}
	}

}
