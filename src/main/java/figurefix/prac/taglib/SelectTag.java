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

import javax.servlet.jsp.JspException;

import figurefix.prac.taglib.html.HtmlElement;

/**
 * a tag implementation of HTML SELECT
 * attributes:
 * <table border=1 cellspacing=0 cellpadding=1 summary="attributes">
 *   <tr><td> impl </td><td> specify {@link figurefix.prac.taglib.SelectElement SelectElement} implementation </td></tr>
 * </table>
 *
 */
public class SelectTag extends TagBuilder {
	
	private String impl = null;
	
	public void setImpl(String impl) {
		this.impl = impl;
	}

	@Override
	protected String build() throws Exception {
		
		String value = this.getValue();
		
		if(this.impl!=null) {
			Class<?> implc = Class.forName(this.impl);
			Object implo = implc.newInstance();
			if( ! (implo instanceof SelectElement) ) {
				throw new JspException(this.impl+" not instance of SelectElement");
			}
			HtmlElement html = ((SelectElement)implo).build(this.getRequest(), value);
			if(html.isInstanceOf("select")) {
				html.setAttributeList(this.getAttributeList());
				return html.toString();
			} else {
				throw new JspException("output of '"+this.impl+"' not instance of html select");
			}
		}
		
		StringWriter sw = new StringWriter();
		if(this.getJspBody()==null) {
			throw new Exception("no option found for select element");
		}
		this.getJspBody().invoke(sw);
		String optstr = sw.toString();
		if(optstr!=null && optstr.trim().length()>0) {
			HtmlElement htmlelm = new HtmlElement("select", this.getAttributeList());
			return htmlelm.getStartTag() + optstr + htmlelm.getEndTag();	    	
		}
		
		throw new JspException("neither '"+this.impl+"' nor page implementation is valid for this select tag");

	}

}
