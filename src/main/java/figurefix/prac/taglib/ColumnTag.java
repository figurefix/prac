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

import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;

public class ColumnTag extends TagBuilder {
	
	private String type = null;
	private String name = null;
	private String title = null;
	private String align = null;
	private String wrap = null;
	private String maxlength = null;
	private String style = null;
	private String options = null;
	private String href = null;

	@Override
	protected String build() throws Exception {
		
		if(this.type==null || this.type.trim().length()==0) {
			throw new Exception("'type' is a required attribute for column tag");
		}
		
		this.type = this.type.toLowerCase();
		
		Column ci = new Column();
		ci.setType(this.type);
		FormGrid.testColName(this.name);
		ci.setName(this.name);
		ci.setTitle(this.title);

		if("hidden".equals(this.type)) {
			
		} else if("text".equals(this.type)) {
			if(this.align!=null) {
				this.align = this.align.toLowerCase();
				if("left".equals(this.align) || "center".equals(this.align) || "right".equals(this.align)) {
					ci.setAlign(this.align);				
				} else {
					throw new Exception("invalide alignment value '"+this.align+"' for column type 'text', use left/center/right");
				}
			}
			if(this.wrap!=null) {
				this.wrap = this.wrap.toLowerCase();
				if("true".equals(this.wrap) || "wrap".equals(this.wrap)) {
					ci.setWrap(true);
				}
			}
		} else if("input".equals(this.type)) {
			if(this.maxlength!=null) {
				try {
					int mlen = Integer.parseInt(this.maxlength);
					if(mlen>0) {
						ci.setMaxlength(mlen);
					}
				} catch (Exception e) {
					throw new Exception("invalide maxlength for column type 'input', "+e.toString(), e);
				}
			}
			if(this.style!=null && this.style.trim().length()>0) {
				ci.setStyle(this.style);
			}
		} else if("select".equals(this.type)) {
			JspFragment jspbody = this.getJspBody();
			if(this.options!=null && this.options.trim().length()>0) {
				ci.setOptions(this.options);
			} else if(jspbody!=null) {
				StringWriter sw = new StringWriter();
				jspbody.invoke(sw);
				ci.setOptions(sw.toString().replace("\n", "").replace("\r", "").trim().replaceAll(">\\s+<", "><"));
			} else {
				throw new Exception("no options found for select column");
			}
		} else if("href".equals(this.type)) {
			if(this.href!=null && this.href.trim().length()>0) {
				ci.setHref(this.href);
			}
		} else {
			throw new Exception("unknown column type '"+this.type+"'");
		}
		
		String[] dynamic = this.getAttributeList().getAllNames();
		for(int i=0; i<dynamic.length; i++) {
			ci.addEvent(dynamic[i], this.getAttributeList().get(dynamic[i]));
		}
		
		JspTag ptag = this.getParent();
		if(ptag instanceof FormGridTag) {
			FormGridTag grid = (FormGridTag)ptag;
			grid.addColumn(ci);
		} else {
			throw new Exception("COLUMN tag should only appear in FORMGRID tag");
		}
		return null;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAlign(String align) {
		this.align = align;
	}

	public void setWrap(String wrap) {
		this.wrap = wrap;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
