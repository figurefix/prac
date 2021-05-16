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
import java.util.ArrayList;

import javax.servlet.jsp.tagext.JspFragment;

import figurefix.prac.taglib.html.HtmlElement;
import figurefix.prac.taglib.html.TextNode;
import figurefix.prac.util.DataMap;

public final class FormGridTag extends TagBuilder {
	
	private String id = null;
	private FormGrid fgrid = null;
	
	private static String formatScript(String str) {
		if(str==null) {
			return "";
		} else {
			return str.replace("'", "\\'");
		}
	}
	
	void addColumn(Column col) {
		fgrid.addColumn(col);
	}
	
	@Override
	protected String build() throws Exception {

		FormGrid.testGridId(this.id);
		
		Object maybegrid = this.getRequest().getAttribute(this.id);
		ArrayList<DataMap> datalist = null;

		if(maybegrid instanceof FormGrid) {
			this.fgrid = (FormGrid)maybegrid;
			datalist = this.fgrid.getData();
		} else {
			this.fgrid = new FormGrid();
			JspFragment jspbody = this.getJspBody();
			if (jspbody == null) {
				throw new Exception("add at least column to formgrid");
			}
			jspbody.invoke(new StringWriter()); //prevents extra blanks outputted to page
			datalist = FormGrid.bind(this.getRequest(), this.id);
		}

		HtmlElement table = new HtmlElement("table");
		table.setAttribute("id", this.id);
		table.setAttribute("border", "0");
		table.setAttribute("cellspacing", "0");
		table.setAttribute("cellpadding", "0");
		
		HtmlElement jselm = new HtmlElement("script");
		jselm.setAttribute("type", "text/javascript");
		
		if(this.fgrid.getColumns().size()==0) {
			throw new RuntimeException("add at least one column to formgrid");
		}
		
		StringBuilder js = new StringBuilder();
		js.append("\nif(typeof("
				+JsDependency.getJsNameSpace()
				+"FormGrid)=='undefined') {alert('formgrid.js not found');}");
		js.append("\nvar "+this.id+" = new "
				+JsDependency.getJsNameSpace()
				+"FormGrid('"+this.id+"', true); \n");
		
		for(int i=0; i<this.fgrid.getColumns().size(); i++) {
			Column ci = this.fgrid.getColumns().get(i);
			
			if("hidden".equals(ci.getType())) {
				js.append(this.id).append(".addColumn('hidden', '")
					.append( ci.getName() ).append("'); \n");
			
			} else if("text".equals(ci.getType())) {
				js.append(this.id).append(".addColumn('text', '")
					.append( ci.getName() ).append("', '")
					.append( formatScript(ci.getTitle()) ).append("'");
				
				js.append(", '");
				if(ci.getAlign()!=null) {
					js.append(ci.getAlign());						
				}
				if(ci.isWrap()) {
					js.append(",wrap");
				}
				js.append("'");
				js.append("); \n");
				
			} else if("input".equals(ci.getType())) {
				js.append(this.id).append(".addColumn('input', '")
					.append( ci.getName() ).append("', '")
					.append( formatScript(ci.getTitle()) ).append("'");
				
				if(ci.getMaxlength()>0) {
					js.append(", ").append(ci.getMaxlength());
				}
				if(ci.getStyle()!=null) {
					js.append(", '").append(ci.getStyle()).append("'");
				}
				if(ci.getEventsObjectLiteral()!=null) {
					js.append(", ").append(ci.getEventsObjectLiteral());
				}
				js.append("); \n");
				
			} else if("select".equals(ci.getType())) {
				js.append(this.id).append(".addColumn('select', '")
					.append( ci.getName() ).append("', '")
					.append( formatScript(ci.getTitle()) ).append("', '")
					.append( ci.getOptions() ).append("'");
				if(ci.getEventsObjectLiteral()!=null) {
					js.append(", ").append(ci.getEventsObjectLiteral());
				}
				js.append("); \n");
				
			} else if("href".equals(ci.getType())) {
				js.append(this.id).append(".addColumn('href', '")
					.append( ci.getName() ).append("', '")
					.append( formatScript(ci.getTitle()) ).append("', ")
					.append( ci.getHref() ).append("); \n");

			}
		}
		
		for(int i=0; datalist!=null && i<datalist.size(); i++) {
			DataMap dm = datalist.get(i);
			js.append(this.id).append(".add(");
			for(int j=0; j<this.fgrid.getColumns().size(); j++) {
				Column ci = this.fgrid.getColumns().get(j);
				if( j != 0 ) {
					js.append(", ");
				}
				String val = "";
				Object valobj = dm.get(ci.getName());
				if(valobj != null) {
					val = valobj.toString().replace("\\", "\\\\").replace("'", "\\'");
				}
				js.append("'").append(val).append("'");
			}
			js.append("); \n");
		}
		
		jselm.addSubNode(new TextNode(js.toString()));
		return table.toString()+"\n"+jselm.toString();
	}

	public void setId(String id) {
		this.id = id;
	}

}
