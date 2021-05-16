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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

/**
 * attributes:
 * <table border=1 cellspacing=0 cellpadding=1 summary="attributes">
 *   <tr><td> name </td><td> &nbsp; show error with this name in the request</td></tr>
 * </table>
 *
 */
public class ErrorTag extends TagBuilder {
	
	private String name = null;
	
	private boolean matchType(Object o) {
		return o!=null && (o instanceof Throwable || o instanceof PageError);
	}

	@Override
	protected String build() throws Exception {
		
		HttpServletRequest req = this.getRequest();
		ArrayList<Object> errlist = new ArrayList<Object>();
		
		if(this.name!=null) {
			Object o = req.getAttribute(this.name);
			if(o instanceof String || this.matchType(o)) {
				errlist.add((Throwable)o);
			}
		} else {
			Enumeration<?> enm = req.getAttributeNames();
			ArrayList<String> names = new ArrayList<String>();
			while( enm.hasMoreElements() ) {
				names.add((String)enm.nextElement());
			}
			String[] namarr = names.toArray(new String[names.size()]);
			Arrays.sort(namarr); // sort by name
			
			for(int i=0; i<namarr.length; i++) {
				Object attr = req.getAttribute(namarr[i]);
				if(this.matchType(attr) ) {
					errlist.add(attr);
				}				
			}
		}
		
		if(errlist.size()==0) {
			return "<!-- error tag: no error matched -->";
		}
		
		StringBuilder html = new StringBuilder();
		for(int i=0; i<errlist.size(); i++) {
			Object o = errlist.get(i);
			html.append("<p>");
			if(o instanceof String) {
				html.append(((String)o).replace("\n", "<br>"));
			} else if(o instanceof PageError) {
				PageError pe = (PageError)o;
				html.append(pe.getHTML());
			} else if(o instanceof Throwable) {
				Throwable tw = (Throwable)o;
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				tw.printStackTrace(pw);
				html.append(sw.toString().replace("\n", "<br>"));
			}
			html.append("</p>");
		}

		return html.toString();
	}

	public void setName(String name) {
		this.name = name;
	}

}
