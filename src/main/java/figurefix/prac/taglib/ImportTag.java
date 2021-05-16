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

/**
 * utility to import javascript or css file that's up to date.<br>
 * e.g. &lt;tt:import src="/common/js/x.js" /&gt;
 * @author figurefix
 *
 */
public class ImportTag extends TagBuilder {
	
	private static String queryString = String.valueOf(System.currentTimeMillis());
	
	public static void setQueryString(String qs) {
		queryString = qs;
	}
	
	private String src = null;
	
	public void setSrc(String s) {
		this.src = s;
	}

	@Override
	protected String build() throws Exception {		
		StringBuffer html = new StringBuffer();
		if(this.src==null) {
			html.append("<!-- empty src -->");
		} else if(this.src.endsWith(".js")) {
			html.append("<script type=\"text/javascript\" src=\"")
				.append(this.getRequest().getContextPath())
				.append(this.src)
				.append("?").append(ImportTag.queryString)
				.append("\"></script>");
		} else if(this.src.endsWith(".css")) {
			html.append("<link href=\"")
				.append(this.getRequest().getContextPath())
				.append(this.src)
				.append("?").append(ImportTag.queryString)
				.append("\" rel=\"stylesheet\" type=\"text/css\">");
		} else {
			html.append("<!-- not supported resource type "+this.src+" -->");
		}
		return html.toString();
	}

}
