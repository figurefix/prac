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
import javax.servlet.jsp.tagext.JspFragment;

import figurefix.prac.taglib.html.HtmlElement;

public class CustomTag extends TagBuilder {
	
	private String impl = null;
	
	public void setImpl(String impl) {
		this.impl = impl;
	}

	@Override
	protected String build() throws Exception {
		
		String bodystr = "";
		JspFragment jspbody = this.getJspBody();
		if(jspbody!=null) {
			StringWriter sw = new StringWriter();				
			jspbody.invoke(sw);
			bodystr = sw.toString();
		}

		Class<?> implc = Class.forName(this.impl);
		Object implo = implc.newInstance();
		if( ! (implo instanceof CustomElement)) {
			throw new JspException(
				this.impl+" not instance of "+CustomElement.class.getName());
		}
		CustomElement celm = (CustomElement)implo;
		HtmlElement html = celm.build(this.getRequest(), this.getAttributeList(), bodystr);
		if(html==null) {
			throw new JspException("The output of "+this.impl+" is null");
		}
		
		return html.toString();
	}

}
