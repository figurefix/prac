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
 figurefix.prac.Branch(containerDivId);
 figurefix.prac.Branch(containerDivId, clickFunction);
 figurefix.prac.Branch(containerDivId, clickFunction, checkboxFunction);
 figurefix.prac.Branch(containerDivId, clickFunction, checkboxFunction, themeColor);
 */
figurefix.prac.Branch = function() {

	this.common_activeBranch = null;
	this.common_themeColor = "#00F";
	this.common_clickFunc = null;
	this.common_checkboxFunc = null;
	this.common_freezed = false;
	
	this.my_cfg_name = null;
	this.my_cfg_data = null;
	this.my_cfg_checkbox = 0;
	this.my_cfg_iconSrc = null;
	this.my_cfg_iconWidth = null;
	this.my_cfg_iconHeight = null;

	this.my_dom_div = null;
	this.my_dom_tbody = null;
	this.my_dom_divsub = null;
	
	this.my_navi_root = null;
	this.my_navi_parent = null;
	this.my_navi_previous_sibling = null;
	this.my_navi_next_sibling = null;
	this.my_navi_first_child = null;
	this.my_navi_last_child = null;
	this.my_navi_is_active = false;

	if(arguments.length==0) {
		
		alert("arguments error");
		return;
		
	} else if(typeof(arguments[0])=="string") { // for root

		this.my_dom_div = document.getElementById(arguments[0]);
		this.my_dom_divsub = this.my_dom_div;
		if(arguments.length>=2 && typeof(arguments[1])=="function") {
			this.common_clickFunc = arguments[1];
		}
		if(arguments.length>=3 && typeof(arguments[2])=="function") {
			this.common_checkboxFunc = arguments[2];
		}
		if(arguments.length>=4 && typeof(arguments[3])=="string") {
			this.common_themeColor = arguments[3];
		}
		this.my_navi_root = this;
		
	} else if(typeof(arguments[0])=="object") { //for branch function
		
		this.my_cfg_name = arguments[0].name;
		this.my_cfg_data = arguments[0].data;
		this.my_cfg_checkbox = typeof(arguments[0].checkbox)=="number" ? arguments[0].checkbox : 0;
		this.my_cfg_iconSrc = typeof(arguments[0].iconSrc)=="string" ? arguments[0].iconSrc : null;
		this.my_cfg_iconWidth = typeof(arguments[0].iconWidth)=="string" ? arguments[0].iconWidth : null;
		this.my_cfg_iconHeight = typeof(arguments[0].iconHeight)=="string" ? arguments[0].iconHeight : null;
		
		this.my_dom_div = this.makedom(null, "div", "display:block; padding:0px 0px 0px 2px; margin:0px;");
		var tble = this.makedom(this.my_dom_div, "table");
		tble.setAttribute("border", "0");
		tble.setAttribute("cellpadding", "0");
		tble.setAttribute("cellspacing", "0");
		tble.style.cssText = "margin:0px; padding:0px; cursor:pointer;";
		
		this.my_dom_tbody = this.makedom(tble, "tbody");
		this.my_dom_divsub = this.makedom(this.my_dom_div, "div", 
			"display:none; margin:0px 0px 0px 3px; padding:0px 0px 0px 17px;");
		
		var tr = this.makedom(this.my_dom_tbody, "tr");
		
		var tddot = this.makedom(tr, "td", "font-size:0px; width:8px; padding:0px; margin:0;");
		tddot.setAttribute("align", "center");
		tddot.setAttribute("valign", "middle");
		tddot.onclick = this.getClickFunc(this);
		this.makedom(tddot, "div", "display:block; vertical-align:middle; " 
			+"font-size:0px; line-height:0px; " // for ie6
			+"padding:0px; margin:0px; "
			+"width:6px; height:6px; "
			+"border: 1px solid #000; "
			+"background-color:"+arguments[0].themeColor+"; "
			+"border-radius:50%; ");
		
		if(this.my_cfg_checkbox != 0) {
			var tdckb = this.makedom(tr, "td", "padding:0px 0px 0px 3px;");
			var ckboxhtml = "<input type=checkbox"
				+ (this.my_cfg_checkbox>0 ? " checked" : "")
				+ (this.my_cfg_checkbox>=2 || this.my_cfg_checkbox<=-2 ? " disabled" : "")
				+ " />";
			tdckb.innerHTML = ckboxhtml;
			tdckb.firstChild.onclick = this.getCheckboxFunc(this, tdckb.firstChild);
		}
		
		var tdico = this.makedom(tr, "td", "padding:0px;");
		tdico.onclick = this.getClickFunc(this);
		if(typeof(this.my_cfg_iconSrc)=="string") {
			var img = this.makedom(tdico, "img", "padding:0px; margin:0px 0px 0px 3px;");
			img.setAttribute("src", this.my_cfg_iconSrc);
			img.setAttribute("width", typeof(this.my_cfg_iconWidth)=="string" ? this.my_cfg_iconWidth : "16px");
			img.setAttribute("height", typeof(this.my_cfg_iconHeight)=="string" ? this.my_cfg_iconHeight : "16px");			
		}

		var tdnam = this.makedom(tr, "td", "padding:0px;");
		tdnam.onclick = this.getClickFunc(this);
		var spnam = this.makedom(tdnam, "span", 
			"padding:1px; margin:0px 3px 0px 3px; display:inline-block;");
		spnam.innerText = this.my_cfg_name;
		spnam.onmouseover = function() {
			spnam.style.textDecoration = "underline";
		};
		spnam.onmouseout = function() {
			spnam.style.textDecoration = "";
		};
		
	} else {
		
		alert("arguments error");
		return;
		
	}
};

figurefix.prac.Branch.prototype.makedom = function(under, tagname, css) {
	var elm = document.createElement(tagname);
	if(under != null) {
		under.appendChild(elm);	
	}
	if(css != undefined) {
		elm.style.cssText = css;
	}
	if(tagname == "td") {
		elm.setAttribute("align", "center");
		elm.setAttribute("valign", "middle");
		elm.setAttribute("nowrap", "true");
		elm.style.padding = "0";
	}
	return elm;
};

/*
 * parameter list: (name, data, ckbox, icon)
 * name: the display name of this branch, mandatory
 * data: attachment data, optional
 * ckbox: an optional integer for checkbox setting, 
 * 		0 : no checkbox 
 * 		1 : checked
 * 		2 : checked but disabled
 * 		-1 : not checked
 * 		-2 : not checked and disabled
 * icon: also an optional parameter, either a string of image src or an object which has three properties
 * 		iconSrc: a string of image src
 * 		iconWidth: width in pixel, e.g. "16px", optional
 * 		iconHeight: height in pixel, e.g. "16px", optional
 */
figurefix.prac.Branch.prototype.parseArgu = function(arguarr) {
	if(arguarr.length==0) {
		alert("arguments error");
		return null;
	}
	var arguobj = {};
	arguobj.name = arguarr[0];
	if(arguarr.length>=2) {
		arguobj.data = arguarr[1];
	}
	for(var i=2; i<4 && i<arguarr.length; i++) {
		if(typeof(arguarr[i])=="number") { //ckbox config
			arguobj.checkbox = arguarr[i];
		} else if(typeof(arguarr[i])=="string") {
			arguobj.iconSrc = arguarr[i];
		} else if(typeof(arguarr[i])=="object") {
			arguobj.iconSrc = arguarr[i].iconSrc;
			arguobj.iconWidth = arguarr[i].iconWidth;
			arguobj.iconHeight = arguarr[i].iconHeight;
		}
	}
	return arguobj;
};
	
figurefix.prac.Branch.prototype.getClickFunc = function(bch) {
	return function() {
		
		if(bch.root().common_freezed) {
			return;
		}
		
		if(bch.my_dom_divsub.style.display=="none") {
			bch.expand();
		} else {
			if(bch.my_navi_is_active) {
				bch.fold();				
			}
		}
		
		bch.focus();
		
		if(typeof(bch.root().common_clickFunc)=="function") {
			bch.root().common_clickFunc(bch, bch.my_cfg_data);
		}
	};
};

figurefix.prac.Branch.prototype.getCheckboxFunc = function(bch, ckbox) {
	return function() {
		if(typeof(bch.root().common_checkboxFunc)=="function") {
			bch.root().common_checkboxFunc(bch, bch.my_cfg_data, ckbox);
		}
	};
};

figurefix.prac.Branch.prototype.remove = function() {
	if(this.my_navi_parent==null) {
		return this;
	}
	
	this.blur();
	
	if(this.my_navi_previous_sibling!=null) {
		this.my_navi_previous_sibling.my_navi_next_sibling = this.my_navi_next_sibling;
	} else { // i'm first
		this.my_navi_parent.my_navi_first_child = this.my_navi_next_sibling;
	}
	if(this.my_navi_next_sibling!=null) {
		this.my_navi_next_sibling.my_navi_previous_sibling = this.my_navi_previous_sibling;
	} else { // i'm last
		this.my_navi_parent.my_navi_last_child = this.my_navi_previous_sibling;
	}
	
	this.my_navi_parent.my_dom_divsub.removeChild(this.my_dom_div);	
	
	this.my_navi_root = null;
	this.my_navi_parent = null;
	this.my_navi_previous_sibling = null;
	this.my_navi_next_sibling = null;
	return this;
};

figurefix.prac.Branch.prototype.branch = function(bch) { // private function
	this.my_dom_divsub.appendChild(bch.my_dom_div);
	if(this.my_navi_first_child == null) {
		this.my_navi_first_child = bch;
		this.my_navi_last_child = bch;
	} else {
		this.my_navi_last_child.my_navi_next_sibling = bch;
		bch.my_navi_previous_sibling = this.my_navi_last_child;
		this.my_navi_last_child = bch;
	}
	
	bch.my_navi_root = this.my_navi_parent==null ? this : this.my_navi_root;
	bch.my_navi_parent = this;
	return this;
};

figurefix.prac.Branch.prototype.spread = function() {
	var argu = this.parseArgu(arguments);
	argu.themeColor = this.root().common_themeColor;
	var bch = new figurefix.prac.Branch(argu);
	this.branch(bch);
	return this;
};

figurefix.prac.Branch.prototype.spreadto = function() {
	var argu = this.parseArgu(arguments);
	argu.themeColor = this.root().common_themeColor;
	var bch = new figurefix.prac.Branch(argu);
	this.branch(bch);
	return bch;
};

figurefix.prac.Branch.prototype.parent = function() {
	return this.my_navi_parent;
};

figurefix.prac.Branch.prototype.root = function() {
	return this.my_navi_root;
};

figurefix.prac.Branch.prototype.first = function() {
	return this.my_navi_first_child;
};

figurefix.prac.Branch.prototype.last = function() {
	return this.my_navi_last_child;
};

figurefix.prac.Branch.prototype.getActive = function() {
	return this.root().common_activeBranch;
};

figurefix.prac.Branch.prototype.previous = function() {
	return this.my_navi_previous_sibling;
};

figurefix.prac.Branch.prototype.next = function() {
	return this.my_navi_next_sibling;
};

figurefix.prac.Branch.prototype.blur = function() {
	this.my_dom_tbody.firstChild //tr
		.lastChild // name td
		.firstChild // name span
		.style.backgroundColor = "";
	this.my_dom_divsub.style.borderLeft = "none";
	this.my_navi_is_active = false;
	this.root().common_activeBranch = null;
	return this;
};

figurefix.prac.Branch.prototype.focus = function() {
	if(!this.my_navi_is_active) {
		var prev = this.root().common_activeBranch;
		if(prev!=null) {
			prev.blur();
		}

		this.root().common_activeBranch = this;

		this.my_dom_tbody.firstChild //tr
			.lastChild // name td
			.firstChild // name span
			.style.backgroundColor = this.root().common_themeColor;
		this.my_dom_divsub.style.borderLeft = "3px dotted "+this.root().common_themeColor;
		this.my_navi_is_active = true;
	}
	return this;
};

figurefix.prac.Branch.prototype.setFreezed = function(frz) {
	if(typeof(frz)=="boolean") {
		this.root().common_freezed = frz;
	}
};

figurefix.prac.Branch.prototype.expand = function() {
	if(this.my_navi_parent==null) {
		return this;
	}
	if(this.my_dom_divsub.style.display=="none" 
	&& this.my_navi_first_child!=null) {
		this.my_dom_divsub.style.display = "block";	
	}
	this.my_dom_tbody.firstChild // tr
		.firstChild // td
		.firstChild // div
		.style.backgroundColor = "#DDD";
	return this;
};

figurefix.prac.Branch.prototype.fold = function() {
	if(this.my_navi_parent==null) {
		return this;
	}
	this.my_dom_divsub.style.display = "none";
	this.my_dom_tbody.firstChild //tr
		.firstChild // td
		.firstChild // div
		.style.backgroundColor = this.root().common_themeColor;
	return this;
};

figurefix.prac.Branch.prototype.expandAll = function() {
	this.expand();
	var point = this.my_navi_first_child;
	while(point!=null) {
		point.expandAll();
		point = point.my_navi_next_sibling;
	}
	return this;
};

figurefix.prac.Branch.prototype.foldAll = function() {
	this.fold();
	var point = this.my_navi_last_child;
	while(point!=null) {
		point.foldAll();
		point = point.my_navi_previous_sibling;
	}
	return this;
};

figurefix.prac.Branch.prototype.getName = function() {
	return this.my_cfg_name;
};

figurefix.prac.Branch.prototype.rename = function(name) {
	this.my_cfg_name = name;
	this.my_dom_tbody.firstChild
		.lastChild // td
		.firstChild // span
		.innerText = name;
	return this;
};

figurefix.prac.Branch.prototype.getData = function() {
	return this.my_cfg_data;
};

figurefix.prac.Branch.prototype.setData = function(data) {
	this.my_cfg_data = data;
	return this;
};

figurefix.prac.Branch.prototype.setIcon = function(src, w, h) {
	var img = this.my_dom_tbody.firstChild // tr
		.lastChild // name td
		.previousSibling // icon td
		.firstChild;
	img.setAttribute("src", src);
	if(w != undefined) {
		img.setAttribute("width", w);
	}
	if(h != undefined) {
		img.setAttribute("height", h);
	}
	return this;
};

figurefix.prac.Branch.prototype.setCheckbox = function(v) {
	if(this.my_cfg_checkbox!=0 && v!=0 && this.my_cfg_checkbox!=v) {
		var ckbox = this.my_dom_tbody.firstChild // tr
						.firstChild // dot td
						.nextSibling // checkbox td
						.firstChild;
		ckbox.checked = (v>0);
		ckbox.disabled = (v<-1 || v>1);
	}
};
