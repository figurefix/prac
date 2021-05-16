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

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * attributes:
 * <table border=1 cellspacing=0 cellpadding=1 summary="attribute list">
 *   <tr><td> exists </td><td> &nbsp; if the specified attribute exists in request</td></tr>
 *   <tr><td> equals </td><td> &nbsp; if the attribute value equals the specified value</td></tr>
 * </table>
 *
 */
@SuppressWarnings("serial")
public final class IfTag extends TagSupport {
	
	private String exists = null;
	private String equals = null;
	private String within = null;
	private String includes = null;
	private String logic = null;
	
	public void setExists(String exists) {
		this.exists = exists;
	}

	public void setEquals(String equals) {
		this.equals = equals;
	}

	public final void setWithin(String within) {
		this.within = within;
	}

	public final void setIncludes(String includes) {
		this.includes = includes;
	}

	public final void setLogic(String logic) {
		this.logic = logic;
	}

	@Override
	public int doStartTag() throws JspException {
		boolean m = this.match();
		if(this.getParent() instanceof CaseTag) {
			CaseTag ctag = (CaseTag)(this.getParent());
			ctag.setIfCase(m);
		}
		return m ? Tag.EVAL_BODY_INCLUDE : Tag.SKIP_BODY;
	}

	private boolean match() {
		ServletRequest req = this.pageContext.getRequest();
		Object val = req.getParameter(this.exists);
		if(val==null) {
			val = req.getAttribute(this.exists);
		}
		
		boolean exist = (val != null);
		if(!exist) {
			return false;
		} else if(this.equals==null && this.within==null && this.includes==null) {
			return true;
		} else {
			if("and".equals(this.logic)) {
				return (this.equals!=null ? val.toString().equals(this.equals) : true) 
					&& (this.within!=null ? this.within.contains(val.toString()) : true) 
					&& (this.includes!=null ? val.toString().contains(this.includes) : true);
			} else {
				return (this.equals!=null ? val.toString().equals(this.equals) : false) 
					|| (this.within!=null ? this.within.contains(val.toString()) : false) 
					|| (this.includes!=null ? val.toString().contains(this.includes) : false);
			}
		}
	}

}
