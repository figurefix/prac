/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 */

var figurefix = window.figurefix || {};
figurefix.prac = figurefix.prac || {};

/*
	construction:
	var la = new figurefix.prac.Label(theme);
	or
	var la = new figurefix.prac.Label(mystyle);
	
	theme: 1 - 8 int
	mystyle.theme
	mystyle.backgroundColor  string
	mystyle.color  string
	mystyle.fontFamily  string
	mystyle.fontWeight  string
	mystyle.fontStyle  string
 */
figurefix.prac.Label = function(mystyle) {
	this.mystyle = mystyle;
	var tof = typeof(mystyle);
	if(tof=="number") {
		this.mystyle = {};
		this.mystyle.theme = mystyle;
	} else if(tof=="object") {
		this.mystyle = mystyle;
	} else {
		this.mystyle = {};
	}
	
	var agentInfo = navigator.userAgent;
	this.isOldIE = (agentInfo.indexOf("MSIE 6.")!=-1 
					|| agentInfo.indexOf("MSIE 7.")!=-1);
};

/*
  setup(div_id)
  setup(div_id, ext_element)
 */
figurefix.prac.Label.prototype.setup = function(bid, extelm) {
	
	var celm = document.getElementById(bid);
	var name = celm.getAttribute("title");
	celm.removeAttribute("title");
	
	var bdiv = document.createElement("div");
	celm.parentNode.insertBefore(bdiv, celm);
	
	bdiv.style.display = "block";
	bdiv.style.width = "99%";
	bdiv.style.marginTop = "20px";
	bdiv.style.marginBottom = "20px";
	
	var getStyle = function(value, dftval) {
		return typeof(value)=="string" ? value : dftval;
	};
	var makelm = function() {
		var pnode = arguments[0];
		var cnode = null;
		for(var i=1; i<arguments.length; i++) {
			var tagname = arguments[i];
			cnode = document.createElement(tagname);
			if(tagname=="table") {
				cnode.setAttribute("border", "0");
				cnode.setAttribute("cellspacing", "0");
				cnode.setAttribute("cellpadding", "0");
			}
			if(tagname=="table" || tagname=="tbody" 
			|| tagname=="tr" || tagname=="td") {
				cnode.style.height = "";
			}
			pnode.appendChild(cnode);
			pnode = cnode;
		}
		return cnode;
	};
	
	var bgc = getStyle(this.mystyle.backgroundColor, "#069");
	
	var tb = makelm(bdiv, "table");
	var tbody = makelm(tb, "tbody");
	var tr = makelm(tbody, "tr");
	var td = makelm(tr, "td");
	
	tb.style.width = "100%";
	tb.style.borderStyle = "solid";
	tb.style.borderColor = bgc;
	
	td.setAttribute("align", "left");
	td.setAttribute("valign", "middle");
	td.style.padding = "0";
	td.style.margin = "0";
	td.style.whiteSpace = "nowrap";
	td.style.border = "0";
	td.style.height = "1.8em";
	
	var maintb = makelm(td, "table");
	var maintbd = makelm(maintb, "tbody");
	var maintr = makelm(maintbd, "tr");
	var maintd = makelm(maintr, "td");

	maintd.style.border = "0";
	
	var tagL = document.createElement("div");		
	var tag = document.createElement("div");
	var tagR = document.createElement("div");
	
	var namespan = makelm(tag, "span");
	namespan.innerText = name;
	
	bdiv.style.display = "block";
	
	tagL.style.display = "inline-block";
	tagL.style.height = "0";
	tagL.style.lineHeight = "0";
	tagL.style.width = "0";
	tagL.style.margin = "0";
	tagL.style.padding = "0";
	tagL.style.borderStyle = "solid";
	tagL.style.borderWidth = "0.9em 0.4em 0.9em 0.4em";
	tagL.style.borderLeftColor = "transparent";
	tagL.style.borderRightColor = bgc;
	
	tagR.style.display = "inline-block";
	tagR.style.height = "0";
	tagR.style.lineHeight = "0";
	tagR.style.width = "0";
	tagR.style.margin = "0";
	tagR.style.padding = "0";
	tagR.style.borderStyle = "solid";
	tagR.style.borderWidth = "0.9em 0.4em 0.9em 0.4em";
	tagR.style.borderRightColor = "transparent";
	tagR.style.borderLeftColor = bgc;
	
	tag.style.display = "inline-block";
	tag.style.margin = "0";
	tag.style.verticalAlign = "middle";
	tag.style.height = "1.8em";
	tag.style.border = "0";
	tag.style.padding = "0 0.6em 0 0.6em";
	tag.style.backgroundColor = bgc;
	
	tag.style.color = getStyle(this.mystyle.color, "#FFF");
	tag.style.fontFamily = getStyle(this.mystyle.fontFamily, "");
	tag.style.fontSize = "1em"; //getStyle(this.mystyle.fontSize, "1em");
	tag.style.fontWeight = getStyle(this.mystyle.fontWeight, "");
	tag.style.fontStyle = getStyle(this.mystyle.fontStyle, "");
	
	var labelup = (this.mystyle.theme>=5 && this.mystyle.theme<=8);
	
	var labelround = (this.mystyle.theme==4	|| this.mystyle.theme==8);
	
	// 1: tag, 2: tag+tagR, 3: tagL+tag+tagR
	var labelsize = 2;
	if(this.mystyle.theme==1 || this.mystyle.theme==4 
	|| this.mystyle.theme==5 || this.mystyle.theme==8) {
		labelsize = 1;
	} else if(this.mystyle.theme==2 || this.mystyle.theme==6) {
		labelsize = 2;
	} else if(this.mystyle.theme==3 || this.mystyle.theme==7) {
		labelsize = 3;
	}
	
	if(this.isOldIE) {
		labelsize = 1;
		labelround = false;
	}
	
	tb.style.borderWidth = labelup ? "0 0 medium 0" : "medium 0 0 0";
	tag.style.lineHeight = labelup ? "1.8em" : "1.5em";
	tagL.style.verticalAlign = labelup ? "bottom" : "top";
	tag.style.verticalAlign = labelup ? "bottom" : "top";
	tagR.style.verticalAlign = labelup ? "bottom" : "top";
	if(labelround) {
		tag.style.borderRadius = labelup ? "0.6em 0.6em 0 0" : "0 0 0.6em 0.6em";
	}
	if(labelup) {
		tagL.style.borderBottomColor = bgc;
		tagL.style.borderTopColor = "transparent";
		tagR.style.borderBottomColor = bgc;
		tagR.style.borderTopColor = "transparent";
	} else {
		tagL.style.borderTopColor = bgc;
		tagL.style.borderBottomColor = "transparent";
		tagR.style.borderTopColor = bgc;
		tagR.style.borderBottomColor = "transparent";
	}
	
	if(labelsize==1) {
		maintd.appendChild(tag);
	} else if(labelsize==2) {
		maintd.appendChild(tag);
		maintd.appendChild(tagR);
	} else if(labelsize==3) {
		maintd.appendChild(tagL);
		maintd.appendChild(tag);
		maintd.appendChild(tagR);
	}
	
	var tdext = makelm(tr, "td");
	tdext.setAttribute("align", "right");
	tdext.setAttribute("valign", "middle");
	tdext.style.cssText = "padding:0; height:100px; white-space:nowrap; vertical-align:middle;";
	tdext.style.height = "";

	var extbl = makelm(tdext, "table");
	var extbd = makelm(extbl, "tbody");
	var extr = makelm(extbd, "tr");

	if(extelm!=undefined && extelm!=null) {
		var extd1 = makelm(extr, "td");
		extd1.setAttribute("align", "center");
		extd1.setAttribute("valign", "middle");
		extd1.style.cssText = "padding:0;";
		extd1.appendChild(extelm);			
	}

	var extd2 = makelm(extr, "td");
	extd2.setAttribute("align", "center");
	extd2.setAttribute("valign", "middle");
	extd2.innerHTML = "&equiv;";
	extd2.style.width = "30px";
	extd2.style.fontSize = "1em";
	extd2.style.fontWeight = "bold";
	extd2.style.cursor = "pointer";
	extd2.onclick = function(){
		return function() {
			if(celm.style.display == "none") {
				celm.style.display = "block";
				extd2.innerHTML = "&equiv;"; 
				// &equiv; &hellip; &lt; &gt; &and; &or;
			} else {
				celm.style.display = "none";
				extd2.innerHTML = "&hellip;";
			}
		};
	}();
	
	var labelobj = {
		rename: function(newname) {
			namespan.innerText = newname;
		}
	};
	
	return labelobj;
};
