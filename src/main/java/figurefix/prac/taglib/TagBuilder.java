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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import figurefix.prac.taglib.html.AttributeList;

/**
 * abstract tag
 */
public abstract class TagBuilder extends SimpleTagSupport implements DynamicAttributes {
	
	private AttributeList attrlist = new AttributeList();
	
	protected abstract String build() throws Exception;
	
	@Override
    public void doTag() throws JspException, IOException {
		try {
			String html = this.build();
			this.getAttributeList().empty();
			if(html!=null) {
				this.getJspContext().getOut().write(html);				
			}
		} catch (JspException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new JspException(e.getMessage(), e);
		}
	}

	@Override
	public void setDynamicAttribute(String uri, String name, Object val) throws JspException {
		if(val instanceof String) {
			this.attrlist.set(name, (String)val);
		} else {
			this.attrlist.set(name);
		}
	}
	
	protected final AttributeList getAttributeList() {
		return this.attrlist;
	}
	
	protected final HttpServletRequest getRequest() {
		PageContext pc = (PageContext)this.getJspContext();
		HttpServletRequest req = (HttpServletRequest)pc.getRequest();
		return req;
	}
	
	protected final HttpServletResponse getResponse() {
		PageContext pc = (PageContext)this.getJspContext();
		HttpServletResponse rsp = (HttpServletResponse)pc.getResponse();
		return rsp;
	}
	
	protected final HttpSession getSession() {
		PageContext pc = (PageContext)this.getJspContext();
		HttpSession session = pc.getSession();
		return session;
	}
	
	protected boolean contains(String name, String value) {
		if(name==null || value==null) {
			return false;
		}

		HttpServletRequest request = this.getRequest();
		Object reqattr = request.getAttribute(name);
		if(reqattr instanceof String) {
			if( value.equals( (String)reqattr ) ) {
				return true;
			}
		}

		String[] vals = request.getParameterValues(name);
		if( vals != null ) {
			for(int i=0; i<vals.length; i++) {
				if( value.equals( vals[i] ) ) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	protected String getValue(String name) {
		if(name==null) {
			return null;
		}
		String val = null;
		HttpServletRequest request = this.getRequest();
		Object reqattr = request.getAttribute(name);
		if(reqattr instanceof String) {
			val = (String)reqattr;
		} else if(reqattr != null) {
			val = reqattr.toString();
		}
		if(val==null) {
			val = request.getParameter(name);
		}
		return val;
	}
	
	protected String getValue() {
		String name = this.getAttributeList().get("name");
		return this.getValue(name);
	}
}
