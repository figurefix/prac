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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import figurefix.prac.taglib.html.HtmlElement;
import figurefix.prac.taglib.html.TextNode;

/**
 * attributes
 * <table border=1 cellspacing=0 cellpadding=1 summary="attributes">
 *   <tr><td> types </td><td> message match java types sepereated with , </td></tr>
 *   <tr><td> name </td><td> message match name </td></tr>
 *   <tr><td> fgcolor </td><td> message foreground color, e.g. #000 </td></tr>
 *   <tr><td> bgcolor </td><td> message background color, e.g. #FFF </td></tr>
 * </table>
 */
public class MessageTag extends TagBuilder {
	
	private static final String DFT_ERR_FG_COLOR = "#FFF";
	private static final String DFT_ERR_BG_COLOR = "#F60";
	private static final String DFT_TIP_FG_COLOR = "#FFF";
	private static final String DFT_TIP_BG_COLOR = "#93F";
	
	private String types = null;
	private String name = null;
	private String bgcolor = null;
	private String fgcolor = null;
	
	private boolean matchType(Object o) {
		if(o==null) {
			return false;
		}
		if(this.types==null) {
			return (o instanceof Throwable || o instanceof PageMessage);
		} else {
			String[] type = types.split("\\,");
			Class<?> c = o.getClass();
			while(c!=null) {
				for(int i=0; i<type.length; i++) {
					if(c.getName().equals(type[i].trim())) {
						return true;
					}	
				}
				c = c.getSuperclass();
			}			
		}
		return false;
	}
	
	@Override
	protected String build() throws Exception {
		
		HttpServletRequest req = this.getRequest();
		ArrayList<Object> msglist = new ArrayList<Object>();
		if(this.name!=null) {
			Object named = req.getAttribute(this.name);
			if(named==null) {
				named = req.getParameter(this.name);
			}
			if(named!=null) {
				if(named instanceof String || this.matchType(named)) {
					msglist.add(named);
				}				
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
				if( this.matchType(attr) ) {
					msglist.add(attr);
				}				
			}
		}

		int msgcnt = msglist.size();
		if( msgcnt == 0 ) {
			return "<!-- message tag: no message -->";
		}
		
		/* dom structure:
			<div> root div
				<script type="text/javascript">
					...
				</script>
				<div> div1, message div
					<table>
						<tr>
							<td> message content </td>
							<td> <span> &times; </span> </td>
						</tr>
					</table>
					... more message tables
				</div>
				<div> div2, overall control div
					<table> used to fullfill the page width
					<tr>
					<td>
						<table>
							<tr>
								<td>
									<table> hide show icon (three short lines implemented by div)
										<tr><td><div></div></td></tr>
										<tr><td><div></div></td></tr>
										<tr><td><div></div></td></tr>
									</table>
								</td>
								<td> <span> msg count </span> CLEAN ALL </td>
							</tr>
						</table>
					</td>
					</tr>
					</table>
				</div>
			</div>		 
		 */
		
		String id = (MessageTag.class.getName()+String.valueOf(Math.random())).replace(".", "");
		
		HtmlElement rootdiv = new HtmlElement("div");
		rootdiv.setAttribute("style", "display:block;");
		
		HtmlElement jselm = this.appendElement(rootdiv, "script");
		jselm.setAttribute("type", "text/javascript");
		
		StringBuilder js = new StringBuilder();
		js.append("var ").append(id).append(" = {")
			.append("count:").append(msglist.size())
			
			.append(",moverx:function(obj) {")
			.append("	obj.style.color = '#fff'; }")
			
			.append(",moutx:function(obj) {")
			.append("	obj.style.color = '#000'; }")
			
			.append(",delall:function(obj, by) {")
			.append("	var rootdiv = by==1 ? obj.parentNode")
			.append(" : obj.parentNode") // tr
			.append(".parentNode") // tbody
			.append(".parentNode") // table
			.append(".parentNode") // td
			.append(".parentNode") // tr
			.append(".parentNode") // tbody
			.append(".parentNode") // table
			.append(".parentNode") // div2
			.append(".parentNode;")
			.append("rootdiv.style.display = 'none';")
			.append("},del:function(obj) {")
			.append("var tbl = obj.parentNode") // td
			.append(".parentNode") // tr
			.append(".parentNode") // tbody
			.append(".parentNode;")
			.append("var div = tbl.parentNode;")
			.append("div.removeChild(tbl);")
			.append(id).append(".count--;")
			.append("if(").append(id).append(".count==0) { ")
			.append(id).append(".delall(div, 1); } }")
			
			.append(",hideshow:function(obj) {")
			.append("var div1 = obj.parentNode") // td
			.append(".parentNode") // tr
			.append(".parentNode") // tbody
			.append(".parentNode") // table
			.append(".parentNode") // td
			.append(".parentNode") // tr
			.append(".parentNode") // tbody
			.append(".parentNode") // table
			.append(".parentNode") // div2
			.append(".previousSibling;")
			.append("while(div1.nodeType!=1) { div1 = div1.previousSibling; }")
			.append("var td2 = obj.parentNode.nextSibling;")
			.append("while(td2.nodeType!=1) { td2 = td2.nextSibling; }")
			.append("if(div1.style.display=='block') {")
			.append("div1.style.display = 'none';")
			.append("td2.firstChild.innerText = '['+").append(id).append(".count+']';")
			.append("} else {")
			.append("div1.style.display = 'block';")
			.append("td2.firstChild.innerText = ''; } }")
			
			.append(",moverh:function(obj) {")
			.append("obj.style.backgroundColor = '#DDD'; }")
			
			.append(",mouth:function(obj) {")
			.append("obj.style.backgroundColor = ''; }")
			.append("};");
		jselm.addSubNode(new TextNode(js.toString()));
		
		HtmlElement div1 = this.appendElement(rootdiv, "div"); // message div
		div1.setAttribute("style", "display:block; width:100%; padding:0; margin:0;");
		
		for(int i=0; i<msglist.size(); i++) {
			
			Object msg = msglist.get(i);
			
			String msgtxt = null;
			String bgc = null;
			String fgc = null;
			
			if(msg instanceof String) {
				msgtxt = (String)msg;
				bgc = this.bgcolor!=null ? this.bgcolor : DFT_TIP_BG_COLOR;
				fgc = this.fgcolor!=null ? this.fgcolor : DFT_TIP_FG_COLOR;
			} else if(msg instanceof PageMessage) {
				PageMessage pm = (PageMessage)msg;
				msgtxt = pm.getHTML();
				String pmbg = pm.getBgColor();
				String pmfg = pm.getFgColor();
				
				if(pmbg!=null) {
					bgc = pmbg;
				} else if(this.bgcolor!=null) {
					bgc = this.bgcolor;
				} else {
					bgc = (msg instanceof Throwable) ? DFT_ERR_BG_COLOR : DFT_TIP_BG_COLOR;
				}
				
				if(pmfg!=null) {
					fgc = pmfg;
				} else if(this.fgcolor!=null) {
					fgc = this.fgcolor;
				} else {
					fgc = (msg instanceof Throwable) ? DFT_ERR_FG_COLOR : DFT_TIP_FG_COLOR;
				}
				
			} else if(msg instanceof Throwable) {
				Throwable thr = (Throwable)msg;
				msgtxt = thr.getMessage();
				if(msgtxt==null || msgtxt.trim().length()==0) {
					msgtxt = thr.toString();
				}
				bgc = this.bgcolor!=null ? this.bgcolor : DFT_ERR_BG_COLOR;
				fgc = this.fgcolor!=null ? this.fgcolor : DFT_ERR_FG_COLOR;
			} else {
				msgtxt = msg.toString();
				bgc = this.bgcolor!=null ? this.bgcolor : DFT_TIP_BG_COLOR;
				fgc = this.fgcolor!=null ? this.fgcolor : DFT_TIP_FG_COLOR;
			}
			bgc.replace(";", "");
			fgc.replace(";", "");
			
			HtmlElement msgtb = this.appendElement(div1, "table");
			msgtb.setAttribute("border", "0");
			msgtb.setAttribute("cellspacing", "0");
			msgtb.setAttribute("cellpadding", "0");
			msgtb.setAttribute("width", "100%");
			msgtb.setAttribute("style", "margin-bottom:2px; background-color:"+bgc+"; border-radius:0em 0.3em 0em 0.3em;");
			
			HtmlElement msgtr = this.appendElement(msgtb, "tr");
			HtmlElement msgtd = this.appendElement(msgtr, "td");
			msgtd.setAttribute("valign", "top");
			msgtd.setAttribute("align", "left");
			msgtd.setAttribute("style", "padding:1px 10px 2px 10px; margin:0; text-align:left; color:"+fgc+";");
			msgtd.addSubNode(new TextNode(msgtxt));
			
			HtmlElement clotd = this.appendElement(msgtr, "td");
			clotd.setAttribute("valign", "top");
			clotd.setAttribute("align", "right");
			clotd.setAttribute("style", "width:20px; padding:0; margin:0; text-align:right;");
			
			HtmlElement clospn = this.appendElement(clotd, "span");
			clospn.setAttribute("onmousemove", id+".moverx(this)");
			clospn.setAttribute("onmouseout", id+".moutx(this)");
			clospn.setAttribute("onclick", id+".del(this)");
			clospn.setAttribute("style", "color:#000; cursor:default; font-weight:bold; padding:0px 5px 0px 5px;");
			clospn.addSubNode(new TextNode("&times;"));	
		}
		
		HtmlElement div2 = this.appendElement(rootdiv, "div"); // overall control div
		div1.setAttribute("style", "display:block; width:100%; padding:0; margin:0;");
		
		HtmlElement tb0 = this.appendElement(div2, "table");
		tb0.setAttribute("border", "0");
		tb0.setAttribute("cellspacing", "0");
		tb0.setAttribute("cellpadding", "0");
		tb0.setAttribute("style", "width:100%");
		
		HtmlElement tr0 = this.appendElement(tb0, "tr");
		HtmlElement td0 = this.appendElement(tr0, "td");
		td0.setAttribute("align", "right");
		td0.setAttribute("valign", "top");
		td0.setAttribute("style", "padding:0; margin:0; text-align:right; vertical-align:top;");

		HtmlElement ctltb = this.appendElement(td0, "table");
		ctltb.setAttribute("border", "0");
		ctltb.setAttribute("cellspacing", "0");
		ctltb.setAttribute("cellpadding", "0");
		ctltb.setAttribute("style", "margin:0; float:right;");
		
		HtmlElement ctltr = this.appendElement(ctltb, "tr");
		HtmlElement ctltd = this.appendElement(ctltr, "td");
		ctltd.setAttribute("nowrap");
		ctltd.setAttribute("valign", "middle");
		ctltd.setAttribute("align", "right");
		ctltd.setAttribute("style", "padding:0px 3px 2px 3px; cursor:pointer; vertical-align:middle;");
		ctltd.setAttribute("onmouseover", id+".moverh(this)");
		ctltd.setAttribute("onmouseout", id+".mouth(this)");
		
		HtmlElement hidtb = this.appendElement(ctltd, "table");
		hidtb.setAttribute("border", "0");
		hidtb.setAttribute("cellspacing", "0");
		hidtb.setAttribute("cellpadding", "0");
		hidtb.setAttribute("onclick", id+".hideshow(this)");
		hidtb.setAttribute("style", "margin:3px 0px 0px 0px;");
		
		HtmlElement hidtr1 = this.appendElement(hidtb, "tr");
		HtmlElement hidtd1 = this.appendElement(hidtr1, "td");
		hidtd1.setAttribute("align", "right");
		hidtd1.setAttribute("style", "height:2px; padding:1px 8px 1px 0px; font-size:0px;"); // font-size:0px for ie6
		HtmlElement hidiv1 = this.appendElement(hidtd1, "div");
		hidiv1.setAttribute("style", "display:block; font-size:0px; width:20px; height:2px; padding:0; margin:0; border:none; background-color:#000;");
		
		HtmlElement hidtr2 = this.appendElement(hidtb, "tr");
		HtmlElement hidtd2 = this.appendElement(hidtr2, "td");
		hidtd2.setAttribute("align", "right");
		hidtd2.setAttribute("style", "height:2px; padding:1px 4px 1px 4px; font-size:0px;"); // font-size:0px for ie6
		HtmlElement hidiv2 = this.appendElement(hidtd2, "div");
		hidiv2.setAttribute("style", "display:block; font-size:0px; width:20px; height:2px; padding:0; margin:0; border:none; background-color:#000;");
		
		HtmlElement hidtr3 = this.appendElement(hidtb, "tr");
		HtmlElement hidtd3 = this.appendElement(hidtr3, "td");
		hidtd3.setAttribute("align", "right");
		hidtd3.setAttribute("style", "height:2px; padding:1px 0px 1px 8px; font-size:0px;"); // font-size:0px for ie6
		HtmlElement hidiv3 = this.appendElement(hidtd3, "div");
		hidiv3.setAttribute("style", "display:block; font-size:0px; width:20px; height:2px; padding:0; margin:0; border:none; background-color:#000;");
		
		HtmlElement clntd = this.appendElement(ctltr, "td");
		clntd.setAttribute("nowrap");
		clntd.setAttribute("valign", "top");
		clntd.setAttribute("style", "padding:0px 5px 0px 5px; font-weight:lighter; font-size:0.9em; cursor:pointer; vertical-align:top;");
		clntd.setAttribute("onmouseover", id+".moverh(this)");
		clntd.setAttribute("onmouseout", id+".mouth(this)");
		clntd.setAttribute("onclick", id+".delall(this, 2)");
		String lang = this.getRequest().getLocale().getLanguage();
		
		HtmlElement cntspn = this.appendElement(clntd, "span"); // count span
		cntspn.setAttribute("style", "margin:0px 3px 0px 0px;");
		clntd.addSubNode(new TextNode("zh".equals(lang) ? "全部清除" : "CLEAN ALL"));

		return rootdiv.toString();
	}
	
	private HtmlElement appendElement(HtmlElement parent, String elmname) {
		HtmlElement sub = new HtmlElement(elmname);
		parent.addSubNode(sub);
		return sub;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setBgcolor(String bgColor) {
		this.bgcolor = bgColor;
	}

	public void setFgcolor(String fgColor) {
		this.fgcolor = fgColor;
	}

}
