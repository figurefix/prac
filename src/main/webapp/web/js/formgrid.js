/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 */

/*
 * FormGrid combines form element into grid, 
 * and offers convenient functions to modify the grid
 */

var figurefix = window.figurefix || {};
figurefix.prac = figurefix.prac || {};

figurefix.prac.FormGrid = function(id, idOfTable) {
	
	var idregexp = "[A-Za-z0-9_]+";
	var idregexpobj = new RegExp(idregexp, "g");
	if( ! idregexpobj.test(id)) {
		alert("formgrid id does not match /"+idregexp+"/g");
		return;
	}
	
	this.gridId = id;
	this.noMoreColumn = false;
	this.rowSnoCnt = 0;
	this.borderLine = "1px solid #333";
	
	this.tableBased = typeof(idOfTable)=="boolean" && idOfTable;
	
	this.baseTable = this.tableBased 
		? document.getElementById(id) 
		: document.createElement("table");
	this.baseTbody = document.createElement("tbody");
	this.titleRow = document.createElement("tr");
	var tmptd = document.createElement("th"); //choice column
	tmptd.style.paddingLeft = "5px";
	tmptd.style.paddingRight = "5px";
	tmptd.style.borderRight = this.borderLine;
	tmptd.style.borderBottom = this.borderLine;
	tmptd.style.backgroundColor = "#084";
	this.titleCheckbox = document.createElement("input");
	this.titleCheckbox.setAttribute("type", "checkbox");
	this.titleCheckbox.style.color = "#999";
	this.titleCheckbox.style.border = "none";
	this.titleCheckbox.style.backgroundColor = "#084";
	this.titleCheckbox.onclick = function() { // onchange is not supported in ie6
		var drows = tmptd.parentNode.parentNode.childNodes;
		var chked = drows.item(0).firstChild.firstChild.checked;
		for(var i=1; i<drows.length; i++) {
			drows.item(i).firstChild.firstChild.checked = chked;
		}
	};
	this.titleHidden = document.createElement("input");
	this.titleHidden.setAttribute("type", "hidden");
	this.titleHidden.setAttribute("name", this.gridId);
	this.titleHidden.setAttribute("value", "");
	
	this.baseTable.setAttribute("border", "0");
	this.baseTable.setAttribute("cellspacing", "0");
	this.baseTable.setAttribute("cellpadding", "0");
	this.baseTable.style.borderTop = this.borderLine;
	this.baseTable.style.borderLeft = this.borderLine;
	
	this.baseTable.appendChild(this.baseTbody);
	this.baseTbody.appendChild(this.titleRow);
	this.titleRow.appendChild(tmptd);
	tmptd.appendChild(this.titleCheckbox);
	tmptd.appendChild(this.titleHidden);
	
	this.columns = new Array();
};

figurefix.prac.FormGrid.prototype.createTD = function(th) {
	var tagname = th==undefined ? "td" : "th";
	var td = document.createElement(tagname);
	td.setAttribute("valign", "middle");
	td.setAttribute("nowrap", "true");
	td.style.borderRight = this.borderLine;
	td.style.borderBottom = this.borderLine;
	td.style.backgroundColor = "#EEF";
	return td;
};

figurefix.prac.FormGrid.prototype.padTD = function(td) {
	td.style.padding = "3px 5px 3px 5px";
};

figurefix.prac.FormGrid.prototype.addColumn = function(typ, name, title, ext) {
	
	if(this.noMoreColumn) {
		alert("cannot add column after adding data rows");
		return;
	}
	
	var restr = "[A-Za-z0-9_\.\|]+";
	var re = new RegExp(restr, "g");
	if(!re.test(name)) {
		alert("invalide column name, refer the name regular expression /"+restr+"/g");
		return;
	}
	
	var colnamlst = ","+this.titleHidden.value+",";
	if( colnamlst.indexOf(","+name+",") != -1 ) {
		alert("duplicate column name '"+name+"'");
		return;
	} else {
		if(colnamlst==",,") { //empty
			this.titleHidden.value = name;
		} else {
			this.titleHidden.value += (","+name);
		}
	}
	
	var colinfo = {};
	colinfo.name = name;
	colinfo.type = typ;
	colinfo.ext = ext;
	this.columns.push(colinfo);
	
	if("text"==typ) {
		// left/center/right,wrap
		// "left" by default (align to left and no wrap)
//		if(typeof(ext)=="string" && "left"!=ext && "center"!=ext && "right"!=ext) {
//			alert("invalide alignment for type 'text', 'left', 'center' or 'right', 'left' by default");
//			return;
//		}
	} else if("input"==typ) {
		if(arguments.length > 3) {
			for(var i=3; i<arguments.length; i++) {
				var argutype = typeof(arguments[i]);
				if(argutype == "number") { // max length
					colinfo.maxLength = arguments[i];
				} else if(argutype == "string") { //css
					colinfo.css = arguments[i];
				} else if(argutype == "function") { //onchange function
					if(typeof(colinfo.events)!="object") {
						colinfo.events = {};
					}
					colinfo.events.onchange = arguments[i];
				} else if(argutype == "object") { //event functions
					colinfo.events = arguments[i];
				}
			}
		}
	} else if("hidden"==typ) {
		
	} else if("select"==typ) {
		if(typeof(ext)!="string" || ext=="") {
			alert("invalide options string for type 'select', "+
				  "options string format:: g1: a=aa, b=bb ; g2: c=cc,d=dd ...");
			return;
		}
		if(arguments.length == 5) {
			if(typeof(arguments[4])=="function") { // onchange function
				colinfo.events = {};
				colinfo.events.onchange = arguments[4];
			} else if(typeof(arguments[4])=="object") { // event functions
				colinfo.events = arguments[4];	
			}
		}
	} else if("href"==typ) {
		if(typeof(ext)!="function") {
			alert("unknown javascript function for type 'href'");
			return;
		}
	} else {
		alert("invalide column type '"+typ+"', "+
		"either be 'text', 'input', 'hidden', 'select' or 'href' ");
		return;
	}
	
	var tmptd = this.createTD("th");
	tmptd.style.padding = "4px 8px 6px 8px";
	if("hidden"==typ) {
		tmptd.style.display = "none";
	} else {
		tmptd.innerHTML = title;
		tmptd.setAttribute("align", "center");
		tmptd.setAttribute("nowrap", "nowrap");
		tmptd.style.textAlign = "center";
		tmptd.style.backgroundColor = "#084";
		tmptd.style.color = "#fff";
		tmptd.style.fontWeight = "bold";
	}

	this.titleRow.appendChild(tmptd);

};

figurefix.prac.FormGrid.prototype.getEvntFunc = function(tbd, rsno, func) {
	return function() {
		var alltr = tbd.childNodes;
		var idx = 1;
		for(; idx<alltr.length; idx++) { //locate data row index
			var onetr = alltr.item(idx);
			if(onetr.firstChild  //td
			   .firstChild //checkbox
			   .value==(""+rsno)) {
				break;
			}
		}
		func(idx-1); //index for data rows without title row
	};
};

figurefix.prac.FormGrid.prototype.getCkboxFunc = function(btbody, tleckbox, ckbox) {
	return function() {
		if(tleckbox.disabled && ckbox.checked) {
			var allrow = btbody.childNodes;
			for(var i=1; i<allrow.length; i++) {
				var box = allrow.item(i).firstChild.firstChild;
				if(box!=ckbox) {
					box.checked = false;
				}
			}
		}	
	};
};

figurefix.prac.FormGrid.prototype.add = function() {
	if(this.columns.length==0) {
		alert("no column definded for this FormGrid");
		return;
	}
	var dataArr = arguments;
	if(arguments.length==1 && arguments[0] instanceof Array) {
		dataArr = arguments[0];
	}
	if(dataArr.length>0 && dataArr.length!=this.columns.length) {
		alert("the number of arguments should either be 0 (an empty row) "+
		"or equals to the number of columns ("+this.columns.length+")");
		return;
	}
	
	this.noMoreColumn = true;
	
	var newtr = document.createElement("tr");
	var rowsno = this.rowSnoCnt++;
	
	var ckbtd = this.createTD();
	ckbtd.setAttribute("align", "center");
	
	var ckbox = document.createElement("input");
	ckbox.setAttribute("type", "checkbox");
	ckbox.setAttribute("value", ""+rowsno);
	ckbox.onclick = this.getCkboxFunc(this.baseTbody, this.titleCheckbox, ckbox);

	newtr.appendChild(ckbtd);
	ckbtd.appendChild(ckbox);
	
	for(var i=0; i<this.columns.length; i++) {
		
		var colinfo = this.columns[i];
		var formname = this.gridId+"."+colinfo.name+"."+rowsno;
		var newtd = this.createTD();
		var typ = colinfo.type;
		var ext = colinfo.ext;
		var val = "";
		if(dataArr.length>0 && dataArr[i]!=undefined && dataArr[i]!=null) {
			val = dataArr[i];
		}
		
		var createInput = function(t) {
			var ipt = document.createElement("input");
			ipt.setAttribute("type", t);
			ipt.setAttribute("name", formname);
			ipt.setAttribute("value", val);
			return ipt;			
		};
		
		if("text"==typ) {
			this.padTD(newtd);
			var hid = createInput("hidden");
			var spn = document.createElement("span");
			spn.innerHTML = val;
			
			if(typeof(ext)=="string") {
				if(ext.indexOf("left")!=-1) {
					newtd.setAttribute("align", "left");
				} else if(ext.indexOf("center")!=-1) {
					newtd.setAttribute("align", "center");
				} else if(ext.indexOf("right")!=-1) {
					newtd.setAttribute("align", "right");
				}
				spn.style.whiteSpace = (ext.indexOf("wrap")!=-1) ? "normal" : "nowrap";
			}

			newtd.appendChild(hid);
			newtd.appendChild(spn);
			
		} else if("input"==typ) {
			var ipt = createInput("text");
			if(typeof(colinfo.maxLength) == "number") { //max length
				ipt.setAttribute("maxlength", colinfo.maxLength);
			}
			if(typeof(colinfo.css) == "string") { //css
				ipt.style.cssText = colinfo.css;
			}
			if(typeof(colinfo.events) == "object") {
				for(evtfnc in colinfo.events) {
					ipt[evtfnc] = this.getEvntFunc(this.baseTbody, rowsno, (colinfo.events)[evtfnc]);
				}
			}
			ipt.style.border = "none";
//			ipt.style.display = "table-cell"; // not supported in ie6
			ipt.style.verticalAlign = "middle";
			this.padTD(ipt); //do this after cssText assignment via ext2
			ipt.style.height = "98%";
			newtd.appendChild(ipt);

		} else if("hidden"==typ) {
			var hid = createInput("hidden");
			newtd.appendChild(hid);
			newtd.style.display = "none";
			
		} else if("select"==typ) {
			newtd.setAttribute("align", "right"); //make it looks nice when the title is wider than data
			var selelm = document.createElement("select");
			selelm.style.border = "none";
			selelm.style.margin = "0";
			selelm.style.paddingLeft = "5px";
//			selelm.style.display = "table-cell"; // not supported in ie6
			selelm.style.verticalAlign = "middle";
			selelm.setAttribute("name", formname);
			
			var optlowstr = ext.toLowerCase();
			if((optlowstr.indexOf("<option") == 0 
			 || optlowstr.indexOf("<optgroup") == 0) 
			&& optlowstr.indexOf("</option>") != -1 ) {
				selelm.innerHTML = ext;
				
			} else {
				var buildopt = function(p, optstr) {
					var opts = optstr.split(",");
					for(var n=0; n<opts.length; n++) {
						var valnam = opts[n].split("=");
						if(valnam.length==2) {
							var optelm = document.createElement("option");
							optelm.setAttribute("value", valnam[0]);
							optelm.innerText = valnam[1];
							p.appendChild(optelm);
						}
					}
				};

				var optgrps = ext.split(";");
				for(var g=0; g<optgrps.length; g++) {
					var optg = optgrps[g].split(":");
					if( optg.length == 1 ) { //direct options
						buildopt(selelm, optg[0]);
					} else if( optg.length == 2 ) { //group title and group content
						var ogelm = document.createElement("optgroup");
						ogelm.setAttribute("label", optg[0]);
						buildopt(ogelm, optg[1]);
						selelm.appendChild(ogelm);
					}
				}
			}

		//	selelm.setAttribute("value", val);
			newtd.appendChild(selelm);
			selelm.value = val;
			if(typeof(colinfo.events) == "object") {
				for(evtfnc in colinfo.events) {
					selelm[evtfnc] = this.getEvntFunc(this.baseTbody, rowsno, (colinfo.events)[evtfnc]);
				}
			}
			
		} else if("href"==typ) {
			this.padTD(newtd);
			newtd.setAttribute("align", "right");
			var hid = createInput("hidden");
			var spn = document.createElement("span");
			spn.innerHTML = val;
			var tipspn = document.createElement("span");
			tipspn.innerHTML = "&hellip;";
			tipspn.style.paddingLeft = "5px";
			
			newtd.style.cursor = "pointer";
			newtd.appendChild(hid);
			newtd.appendChild(spn);
			newtd.appendChild(tipspn);
			newtd.onclick = this.getEvntFunc(this.baseTbody, rowsno, ext);
		}
		
		newtr.appendChild(newtd);
	}
	
	this.baseTbody.appendChild(newtr);
};

figurefix.prac.FormGrid.prototype.size = function() {
	return this.baseTbody.childNodes.length-1;
};

figurefix.prac.FormGrid.prototype.get = function(selected) {
	var arr = new Array();
	var siz = this.size();
	for(var i=0; i<siz; i++) {
		var ro = new Array();
		var trow = this.baseTbody.childNodes.item(i+1);
		if(typeof(selected)=="boolean" && selected 
		&& ! trow.firstChild // checkbox td
				.firstChild // checkbox
				.checked) {
			continue;
		}
		for(var j=0; j<this.columns.length; j++) {
			var td = trow.childNodes.item(j+1);
			ro.push( td.firstChild.value );
		}
		arr.push(ro);
	}
	return arr;
};

figurefix.prac.FormGrid.prototype.select = function() {
	return this.get(true);
};

figurefix.prac.FormGrid.prototype.copy = function() {
	var seldata = this.select();
	for(var i=0; i<seldata.length; i++) {
		this.add(seldata[i]);
	}
};

figurefix.prac.FormGrid.prototype.selectIndex = function() {
	var arr = new Array();
	var siz = this.size();
	for(var i=0; i<siz; i++) {
		var trow = this.baseTbody.childNodes.item(i+1);
		if(trow.firstChild // checkbox td
				.firstChild // checkbox
				.checked) {
			arr.push(i);
		}
	}
	return arr;
};

figurefix.prac.FormGrid.prototype.update = function() {
	if( arguments.length != (this.columns.length + 1) ) {
		alert("number of arguments error");
		return;
	}
	var dataRowIdx = arguments[0];
	var siz = this.size();
	if( dataRowIdx >= siz ) {
		alert("data row index out of range");
		return;
	}
	var trow = this.baseTbody.childNodes.item(dataRowIdx+1);
	for(var j=0; j<this.columns.length; j++) {
		var val = arguments[j+1];
		var colinfo = this.columns[j];
		var typ = colinfo.type;
		var td = trow.childNodes.item(j+1);
		if("text"==typ) {
			td.firstChild.value = val;
			td.lastChild.innerHTML = val;

		} else if("input"==typ) {
			td.firstChild.value = val;

		} else if("hidden"==typ) {
			td.firstChild.value = val;

		} else if("select"==typ) {
			td.firstChild.value = val;

		} else if("href"==typ) {
			td.firstChild.value = val;
			td.firstChild.nextSibling.innerHTML = val;
		}
	}
};

figurefix.prac.FormGrid.prototype.set = function(dataRowIdx, colname, val) {
	var siz = this.size();
	if( dataRowIdx >= siz ) {
		alert("data row index out of range");
		return;
	}
	var trow = this.baseTbody.childNodes.item(dataRowIdx+1);
	for(var j=0; j<this.columns.length; j++) {
		var colinfo = this.columns[j];
		var typ = colinfo.type;
		if ( colinfo.name == colname ) {
			var td = trow.childNodes.item(j+1);
			if("text"==typ) {
				td.firstChild.value = val;
				td.lastChild.innerHTML = val;

			} else if("input"==typ) {
				td.firstChild.value = val;

			} else if("hidden"==typ) {
				td.firstChild.value = val;

			} else if("select"==typ) {
				td.firstChild.value = val;

			} else if("href"==typ) {
				td.firstChild.value = val;
				td.firstChild.nextSibling.innerHTML = val;
			}
			break;
		}
	}
};

figurefix.prac.FormGrid.prototype.remove = function(dataRowIdx) {
	if( typeof(dataRowIdx)=="number" ) {
		var siz = this.size();
		if( dataRowIdx < siz ) {
			var trow = this.baseTbody.childNodes.item(dataRowIdx+1);
			this.baseTbody.removeChild(trow);
		}		
	} else { //remove selected
		for(var i=1; i<this.baseTbody.childNodes.length; i++) {
			var trow = this.baseTbody.childNodes.item(i);
			if(trow.firstChild // checkbox td
					.firstChild // checkbox
					.checked) {
				this.baseTbody.removeChild(trow);
				i--;
			}
		}	
	}
};

figurefix.prac.FormGrid.prototype.empty = function() {
	var len = this.baseTbody.childNodes.length;
	while(len>1) {
		this.baseTbody.removeChild(this.baseTbody.lastChild);
		len--;
	}
	this.titleRow.firstChild.firstChild.checked = false;
};

figurefix.prac.FormGrid.prototype.setRadioMode = function(onoff) {
	var bo = typeof(onoff)=="boolean" ? onoff : true;
	this.titleCheckbox.disabled = bo;
	if(bo) {
		var allrow = this.baseTbody.childNodes;
		for(var i=0; i<allrow.length; i++) {
			allrow.item(i).firstChild.firstChild.checked = false;
		}
	}
};

figurefix.prac.FormGrid.prototype.reverseSelection = function() {
	var allrows = this.baseTbody.childNodes;
	for(var i=1; i<allrows.length; i++) {
		var chkbox = allrows.item(i).firstChild.firstChild;
		chkbox.checked = ! chkbox.checked;
	}
};

figurefix.prac.FormGrid.prototype.appendTo = function(homeid) {
	if ( ! this.tableBased ) {
		document.getElementById(homeid).appendChild(this.baseTable);		
	}
};

