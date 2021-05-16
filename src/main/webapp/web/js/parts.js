/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 */

var figurefix = window.figurefix || {};
figurefix.prac = figurefix.prac || {};

figurefix.prac.Parts = function(xTag, xEsc) {
	this.tag = '|';
	this.esc = ':';
	if(typeof(xTag)=="string" && xTag.length==1
	&& typeof(xEsc)=="string" && xEsc.length==1
	&& xTag!=xEsc
	) {
		this.tag = xTag;
		this.esc = xEsc;
	}
};
figurefix.prac.Parts.prototype.isJoined = function(str) {
	if(typeof(str)=="string" && str.length>0 && str.charAt(0)==this.tag) {
		return true;
	}
	return false;
};
figurefix.prac.Parts.prototype.join = function() {
		var out = "";
		for(var i=0; i<arguments.length; i++) {
			if(arguments[i] instanceof Array) {
				var arr = arguments[i];
				for(var j=0; j<arr.length; j++) {
					var tmp = this.join(arr[j]);
					out = out.concat(tmp);
				}
			} else {
				out = out.concat(this.tag);
				var s = "".concat(arguments[i]); // transfer to string
				for(var k=0; k<s.length; k++) {
					var c = s.charAt(k);
					if(c==this.tag || c==this.esc) {
						out = out.concat(this.esc);
					}
					out = out.concat(c);
				}
			}
		}
		return out;
};
figurefix.prac.Parts.prototype.part = function(st) {
	if(typeof(st)!="string" || st.length==0 || st.charAt(0)!=this.tag) {
		return new Array();		
	}
	
	var list = new Array();
	var buf = "";
	var idx = 1;
	while( idx < st.length ) {
		var c = st.charAt(idx);
		if(c==this.esc) {
			buf = buf.concat(st.charAt(idx+1));
			idx += 2;
		} else if(c==this.tag) {
			list[list.length] = buf;
			buf = "";
			idx++;
		} else {
			buf = buf.concat(c);
			idx++;
		}
	}
	list[list.length] = buf;
	
	return list;
};

