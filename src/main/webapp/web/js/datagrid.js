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
 * constructor for figurefix.prac.DataGrid, supports two types of arguments combinations:
 * figurefix.prac.DataGrid(containerId, title0, title1 ...)
 * or
 * figurefix.prac.DataGrid(configObject, title0, title1 ...)
 * where configObject is an object who may contains following properties: 
 * 		homeId  string  required
 * 		fixTitle  boolean
 * 		order  boolean
 * 		oneClickFocus  boolean
 * 		themeColor  string start with #
 * 		focusColor  string start with #
 * 		borderColor  string start with #
 * for example: var configObject = { homeId : "containerId", fixTitle : false};
 */
figurefix.prac.DataGrid = function() {
	this.tdGrid = new Array();
	this.order = true;
	
	this.ordered = new Array(); //record the order state for every column. 0 not yet or descending, 1 ascending
	this.colStyle = new Array();
	this.colType = new Array();
	this.cmpFunc = new Array();
	
	this.fixTitle = true;
	this.focusTr = null;
	this.focusColor = "#B1CEB0";
	this.themeColor = "#084";
	this.borderColor = "#000";
	this.borderStyle = "1px solid "+this.borderColor;
	
	
	var objarg = typeof(arguments[0])=="object";
	if(objarg) {
		if(typeof(arguments[0].fixTitle)=="boolean") {
			this.fixTitle = arguments[0].fixTitle;			
		}
		if(typeof(arguments[0].order)=="boolean") {
			this.order = arguments[0].order;			
		}
		if(typeof(arguments[0].themeColor)=="string") {
			this.themeColor = arguments[0].themeColor;			
		}
		if(typeof(arguments[0].focusColor)=="string") {
			this.focusColor = arguments[0].focusColor;			
		}
		if(typeof(arguments[0].borderColor)=="string") {
			this.borderColor = arguments[0].borderColor;			
		}
	}
	
	var homeId = objarg ? arguments[0].homeId : arguments[0];
	var thome = document.getElementById( homeId );
	var tableBased = objarg && typeof(arguments[0].tableBased)=="boolean" && arguments[0].tableBased;
	var tbl = tableBased ? document.getElementById(homeId) : document.createElement("table");
	this.tableBody = document.createElement("tbody");
	this.titleRow = document.createElement("tr");
	this.tableCols = arguments.length - 1;
	
	var getOrderfunc = function(tobj, col) {
		return function() {
			var hasColCmpFunc = ( typeof(tobj.cmpFunc[col])=="function" );
			if(tobj.tdGrid.length<2 || (!tobj.order && !hasColCmpFunc)) {
				return;
			}
			var val = new Array();
			for(var v=0; v<tobj.tdGrid.length; v++) {
				if(tobj.colType[col]=="number") {
					val[v] = parseFloat(tobj.tdGrid[v][col].innerHTML);
				} else if(tobj.colType[col]=="object" && hasColCmpFunc) {
					val[v] = tobj.tdGrid[v][col].firstChild;
				} else {
					val[v] = tobj.tdGrid[v][col].innerHTML;
				}
			}
			
			var cmp = hasColCmpFunc ? tobj.cmpFunc[col] : function(va, vb) {
				if(va < vb) {
					return -1;
				} else if(va > vb) {
					return 1;
				} else { // equals
					return 0;
				}
			};
			
			for(var j=0; j<val.length-1; j++) {
				var mov = j;
				for(var k=j+1; k<val.length; k++) {
					var cmpval = cmp(val[k], val[mov]);
					if(( tobj.ordered[col]==0 && cmpval==-1 ) 
					|| ( tobj.ordered[col]==1 && cmpval==1 )) {
						mov = k; //find minimal or maximal
					}
				}
				if(mov != j) { // move data and table rows
					var tmp0 = val[j];
					val[j] = val[mov];
					val[mov] = tmp0;
					var tmp1 = tobj.tdGrid[j];
					tobj.tdGrid[j] = tobj.tdGrid[mov];
					tobj.tdGrid[mov] = tmp1;
				}
			}
			
			tobj.ordered[col] = 1 - tobj.ordered[col];

			// display update
			var tmptable = tobj.tableBody.parentNode;
			tmptable.removeChild(tobj.tableBody);
			tobj.tableBody = document.createElement("tbody");
			tobj.tableBody.appendChild(tobj.titleRow);
			for(var k=0; k<tobj.tdGrid.length; k++) {
				tobj.tableBody.appendChild(tobj.tdGrid[k][0].parentNode);
			}
			tmptable.appendChild(tobj.tableBody);
		};
	};
	
	var getOverFunc = function(td_elm, t_obj, col_i) {
		return function() {
			if(t_obj.order || typeof(t_obj.cmpFunc[col_i])=="function") {
				td_elm.style.textDecoration = "underline";
			}			
		};
	};
	
	for(var i=1; i<arguments.length; i++) {
		var td = document.createElement("th");
		td.setAttribute("align", "center");
		td.style.cssText = "cursor: pointer; border-right:"+this.borderStyle+"; border-bottom:"+this.borderStyle+"";
		td.style.padding = "4px 8px 6px 8px";
		td.innerHTML = ""+arguments[i];
		
		td.onmouseover = getOverFunc(td, this, i-1);
		td.onmouseout = function() {
			this.style.textDecoration = "none";
		};

		this.ordered[i-1] = 0;
		td.onclick = getOrderfunc(this, i-1);
		
		this.titleRow.appendChild(td);
	}
	this.tableBody.appendChild(this.titleRow);
	tbl.appendChild(this.tableBody);
	if ( ! tableBased ) {
		thome.appendChild(tbl);
	}
	tbl.cellSpacing = "0";
	tbl.cellPadding = "3";
	tbl.setAttribute("border", "0");
	tbl.style.cssText = "border-top:"+this.borderStyle+"; border-left:"+this.borderStyle+"";
	this.titleRow.style.cssText = "background-color:"+this.themeColor+"; color:#EEE; font-weight:bold";
};
figurefix.prac.DataGrid.prototype.setFixTitle = function(b) {
	this.fixTitle = b;
};
figurefix.prac.DataGrid.prototype.isFixTitle = function() {
	return this.fixTitle;
};
figurefix.prac.DataGrid.prototype.setOrder = function(o) {
	this.order = o;
};
figurefix.prac.DataGrid.prototype.isOrder = function() {
	return this.order;
};
figurefix.prac.DataGrid.prototype.setTitleStyle = function(str) {
	this.titleRow.style.cssText = str;
};
figurefix.prac.DataGrid.prototype.setColStyle = function(idx, str) {
	if(idx<0 || idx>=this.tableCols) {
		return;
	}
	this.colStyle[idx] = str;
	for(var i=0; i<this.tdGrid.length; i++) {
		this.tdGrid[i][idx].style.cssText = str;
		this.tdGrid[i][idx].style.borderRight = this.borderStyle;
		this.tdGrid[i][idx].style.borderBottom = this.borderStyle;
	}
};
figurefix.prac.DataGrid.prototype.setFocusColor = function(str) {
	this.focusColor = str;
};
/*
 * set the comparison function for column "col" (col starts from 0)
 * the comparison function should accept two values as parameters and should return -1, 0 or 1
 * -1: less than
 * 0: equals
 * 1: great than
 */
figurefix.prac.DataGrid.prototype.setCmpFunc = function(col, func) {
	if(col>=this.tableCols || typeof(func)!="function") {
		return;
	}
	this.cmpFunc[col] = func;
};
figurefix.prac.DataGrid.prototype.append = function() {
	var tdarr = new Array();
	var tr = document.createElement("tr");
	for(var i=0; i<this.tableCols; i++) {
		var td = document.createElement("td");
		if(i<arguments.length) {
			var type = typeof(arguments[i]);
			if(type=="function") {
				td.appendChild(arguments[i]());
			} else if(type=="object") {
				td.appendChild(arguments[i]);
			} else {
				td.innerHTML = arguments[i];				
			}
			if(this.colType[i]==undefined) {
				this.colType[i] = type;				
			} else if(this.colType[i] != type) {
				alert("column "+i+" type mismatch with previous row");
				return;
			}
		} else {
			td.innerHTML = "";
			if(this.colType[i]==undefined) {
				this.colType[i] = "string";				
			} else if(this.colType[i] != "string") {
				alert("column "+i+" type mismatch with previous row");
				return;
			}
		}

		if(this.colStyle[i] != undefined) {
			td.style.cssText = this.colStyle[i];
		}
		td.style.borderRight = this.borderStyle;
		td.style.borderBottom = this.borderStyle;
		td.style.paddingLeft = "8px";
		td.style.paddingRight = "8px";
		tr.appendChild(td);
		tdarr[tdarr.length] = td;
	}
	this.tdGrid[this.tdGrid.length] = tdarr;
	
	// the "over" and "out" function works slowly when there are many data rows

	tr.onclick = function(tobj, newtr) {
		return function() {
			if(tobj.fixTitle) {
				if(tobj.focusTr!=null) {
					tobj.focusTr.style.backgroundColor = "";
				}
				newtr.style.backgroundColor = tobj.focusColor;
				tobj.focusTr = newtr;
			} else {
				tobj.tableBody.removeChild(tobj.titleRow);
				tobj.tableBody.insertBefore(tobj.titleRow, newtr);
			}
		};
	}(this, tr);
	
	this.tableBody.appendChild(tr);
};
figurefix.prac.DataGrid.prototype.remove = function(idx) {
	if(idx<0 || idx>=this.tdGrid.length) {
		return;
	}
	var newGrid = new Array();
	for(var i=0; i<this.tdGrid.length; i++) {
		if(i!=idx) {
			newGrid[newGrid.length] = this.tdGrid[i];			
		}
	}
	this.tableBody.removeChild(this.tdGrid[idx][0].parentNode);
	this.tdGrid = newGrid;
};
figurefix.prac.DataGrid.prototype.setValue = function(row, col, val) {
	if(row<0 || row>=this.tdGrid.length || col<0 || col>=this.tableCols) {
		return;
	}
	if(this.colType[col] != typeof(val)) {
		alert("type mismatch with column "+col);
		return;
	}
	var td = this.tdGrid[row][col];
	if(this.colType[col]=="object") {
		td.removeChild(td.lastChild);
		td.appendChild(val);
	} else {
		td.innerHTML = val;		
	}
};
figurefix.prac.DataGrid.prototype.getValue = function(row, col) {
	if(row<0 || row>=this.tdGrid.length || col<0 || col>=this.tableCols) {
		return null;
	}
	if(this.colType[col] == "object") {
		return this.tdGrid[row][col].firstChild;
	} else if(this.colType[col] == "number") {
		var txtval = this.tdGrid[row][col].innerHTML;
		if(txtval.indexOf(".") != -1) {
			return parseFloat(txtval);			
		} else {
			return parseInt(txtval);
		}
	} else {
		return this.tdGrid[row][col].innerHTML;
	}
};
figurefix.prac.DataGrid.prototype.createLinkSpan = function(val, clkfunc) {
	var spn = document.createElement("span");
	spn.innerHTML = val;
	spn.className = "linkstyle";
	spn.style.cursor = "pointer";
	spn.onclick = clkfunc;
	return spn;
};
