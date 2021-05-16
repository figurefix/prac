/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 */

/*
 * this js offers the information of the current browser
 * figurefix.prac.browser.name will be either "ie", "firefox", "opera", "chrome" or "safari"
 * figurefix.prac.browser.version is the main version number of current browser
 * figurefix.prac.browser.is(name,version) method returns true or false
 */

var figurefix = window.figurefix || {};
figurefix.prac = figurefix.prac || {};

figurefix.prac.Browser = function() {
	this.name = "";
	this.version = 0;

	var info = navigator.userAgent;
	var idx1 = -1;
	if(info.indexOf("MSIE")!=-1) {
		this.name = "ie";
		idx1 = info.indexOf("MSIE")+5;	
	} else if(info.indexOf("Firefox")!=-1) {
		this.name = "firefox";
		idx1 = info.indexOf("Firefox")+8;
	} else if(info.indexOf("Opera")!=-1) {
		this.name = "opera";
		idx1 = info.indexOf("Version")+8;
	} else if(info.indexOf("Chrome")!=-1) {
		this.name = "chrome";
		idx1 = info.indexOf("Chrome")+7;
	} else if(info.indexOf("Safari")!=-1) {
		this.name = "safari";
		idx1 = info.indexOf("Version")+8;
	}
	if(idx1!=-1) {
		var idx2 = info.indexOf(".", idx1);
		if(idx2>idx1) {
			var ver = info.substring(idx1, idx2);
			this.version = parseInt(ver);
		}
	} else {
		this.name = "unknown";
		this.version = 0;
	}

	this.is = function(name, version) {
		if(version==undefined) {
			return this.name==name;
		} else {
			return this.name==name && this.version==version;	
		}
	};
};

