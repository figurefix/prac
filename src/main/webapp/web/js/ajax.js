/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 */

var figurefix = window.figurefix || {};
figurefix.prac = figurefix.prac || {};

figurefix.prac.Ajax = function(asyn, nocashe) {	
	this.async = typeof(asyn)=="boolean" ? asyn : true;
	
	this.xhr = null;
	if (window.XMLHttpRequest) {
		this.xhr = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		//For older Microsoft browsers (IE 5 and 6)
		this.xhr = new ActiveXObject("Microsoft.XMLHTTP");
	} else {
		alert("unable to get XMLHttpRequest object");
		return;
	}
	if(typeof(nocashe)=="boolean" && nocashe) {
		this.xhr.setRequestHeader("Cache-Control", "no-cache");
	}
};

figurefix.prac.Ajax.prototype.setRequestHeader = function(name, value) {
	if(this.xhr!=null) {
		this.xhr.setRequestHeader(name, value);
	}
};

/* 
 * set callback function for specific readyState and status
 * 
 * readyState:
 * 0 Uninitialized The request has not yet been sent
 * 1 Loading The response has not yet arrived
 * 2 Loaded Response headers can be read
 * 3 Interactive Response body is incomplete, but can be read
 * 4 Complete Response body is complete
 * 
 * status:
 * 200 OK
 * 201 Created
 * 400 Bad Request
 * 404 Not Found
 * 500 Internal Server Error
 * ...
 * 
 */

figurefix.prac.Ajax.prototype.setupCallback = function(xhr, state, callback) {
	return function() {
		if(xhr.readyState==state) {
			callback(xhr);
		}
	};
};

/*
 * make a request
 */
figurefix.prac.Ajax.prototype.send = function(method, url, data, asyn, state, callback) {
	if(this.xhr==null) {
		return;
	}
	this.xhr.onreadystatechange = this.setupCallback(this.xhr, state, callback);
	this.xhr.open(method, url, typeof(aysn)=="boolean" ? asyn : this.async);
	this.xhr.send(data);
};

/*
 * make a "get" request
 */
figurefix.prac.Ajax.prototype.get = function(url, aysn, state, callback) {
	this.send("GET", url, null, asyn, state, callback);
};

figurefix.prac.Ajax.prototype.get = function(url, asyn, callback) {
	this.send("GET", url, null, asyn, 4, callback);
};

figurefix.prac.Ajax.prototype.get = function(url, callback) {
	this.send("GET", url, null, this.async, 4, callback);
};

/*
 * make a "post" request
 */
figurefix.prac.Ajax.prototype.post = function(url, data, aysn, state, callback) {
	this.send("POST", url, data, asyn, state, callback);
};

figurefix.prac.Ajax.prototype.post = function(url, data, asyn, callback) {
	this.send("POST", url, data, asyn, 4, callback);
};

figurefix.prac.Ajax.prototype.post = function(url, data, callback) {
	this.send("POST", url, data, this.async, 4, callback);
};

figurefix.prac.Ajax.prototype.getResponseHeader = function(name) {
	if(this.xhr==null) {
		return null;
	}
	return this.xhr.getResponseHeader(name);
};

figurefix.prac.Ajax.prototype.getResponseHeaderNames = function() {
	if(this.xhr==null) {
		return null;
	}
	return this.xhr.getAllResponseHeaders();
};

/*
 * load javascript source from server side
 * current page should contain HTML HEAD element
 * the javascript source should be store as UTF-8 format (ajax transmits UTF-8)
 * url: javascript source url
 * func: callback function after js loaded
 */
figurefix.prac.Ajax.prototype.loadjs = function(url, func) {
	this.setOnSuccess(function(xhr) {
		var head = document.getElementsByTagName("head").item(0);
		var newspt = document.createElement("script");
		newspt.setAttribute("type", "text/javascript");
		newspt.innerText = xhr.responseText;
		head.appendChild(newspt);
		if(typeof(func)=="function") {
			func();			
		}
	});
	this.get(url);
};

