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
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import figurefix.prac.taglib.html.HtmlElement;
import figurefix.prac.util.DataMap;

public final class DataGridTag extends SimpleTagSupport {
	
	private String id = null;
	private DataGrid grid = null;
	
	@Override
    public void doTag() throws JspException, IOException {
		
		PageContext pc = (PageContext)this.getJspContext();
		HttpServletRequest req = (HttpServletRequest)pc.getRequest();
		
		JspWriter jw = this.getJspContext().getOut();
		
		Object maybegrid = req.getAttribute(this.id);
		if(maybegrid instanceof DataGrid) {
			this.grid = (DataGrid)maybegrid;
		} else {
			jw.write("<!-- DataGrid '"+this.id+"' not found in HttpServletRequest -->");
			jw.flush();
			return;
		}

		HtmlElement table = new HtmlElement("table");
		table.setAttribute("id", this.id);
		
		HtmlElement jselm = new HtmlElement("script");
		jselm.setAttribute("type", "text/javascript");
		
		jw.write(table.toString());
		jw.write(jselm.getStartTag());
		
		jw.write("\nif(typeof("
				+JsDependency.getJsNameSpace()
				+"DataGrid)=='undefined'){alert('datagrid.js not found');}");
		jw.write("\nvar "+this.id+" = new "
				+JsDependency.getJsNameSpace()
				+"DataGrid({homeId:'"+this.id+"', tableBased:true");
		jw.write(", order:"+this.grid.isOrder());
		jw.write(", fixTitle:"+this.grid.isFixTitle());
		jw.write("} \n");
		
		ArrayList<DataGridColumn> cols = this.grid.getColumns();
		if ( cols.size() == 0 ) {
			throw new RuntimeException("add at least one column to DataGrid");
		}		
		for(int i=0; i<cols.size(); i++) {
			DataGridColumn col = cols.get(i);
			jw.write(", '"+col.getTitle()+"'");
		}
		jw.write("\n); \n");
		
		for(int i=0; i<cols.size(); i++) {
			DataGridColumn col = cols.get(i);
			String style = col.getStyle();
			if ( style != null ) {
				jw.write(this.id+".setColStyle("+i+", '"+style+"'); \n");
			}
			String cmpfunc = col.getCmpFunction();
			if (cmpfunc != null) {
				jw.write(this.id+".setCmpFunc("+i+", "+cmpfunc+"); \n");
			}
		}
		
		/*
		 *  since the browser executes the script by block,
		 *  devide the script into small blocks, as to improve the page display
		 */
		jw.write(jselm.getEndTag()+jselm.getStartTag()); // commit to page
		
		int block = 0;
		Iterator<DataMap> datalist = this.grid.getData();
		while ( datalist.hasNext() ) {
			if ( (++block) == 10 ) {
				block = 0;
				jw.write(jselm.getEndTag()+jselm.getStartTag()); // commit to page
			}
			DataMap data = datalist.next();
			jw.write(this.id+".append(");
			for (int j=0; j<cols.size(); j++) {
				DataGridColumn col = cols.get(j);
				if ( j != 0 ) {
					jw.write(", ");
				}
				String val = "";
				Object valobj = data.get(col.getName());
				if(valobj != null) {
					val = valobj.toString().replace("\\", "\\\\").replace("'", "\\'");
				}
				if (col.getJsFunction() != null) {
					jw.write("\nfunction() {"
								+"return "+this.id+".createLinkSpan('"+val+"', "
								+"function() {"+col.getJsFunction()+"(");
					String[] jsArgus = col.getJsArgus();
					if (jsArgus == null || jsArgus.length == 0) {
						jw.write("'"+val+"'");
					} else {
						for(int k=0; k<jsArgus.length; k++) {
							if (k != 0) {
								jw.write(", ");
							}
							Object argu = data.get(jsArgus[k]); //process js arguments
							if(argu==null) {
								jw.write("null");
							} else {
								jw.write("'"+argu.toString().replace("\\", "\\\\").replace("'", "\\'")+"'");
							}
						}
					}
					jw.write(");});}\n");
				} else {
					jw.write("'"+val+"'");					
				}
			}

			jw.write("); \n");
		}
		
		jw.write(jselm.getEndTag());
		jw.flush();
	}

	public void setId(String id) {
		this.id = id;
	}

}
