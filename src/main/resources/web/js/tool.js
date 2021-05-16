/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 */

var figurefix = window.figurefix || {};
figurefix.prac = figurefix.prac || {};

figurefix.prac.Tool = function() {
	
};
figurefix.prac.Tool.prototype.trim = function(str) {
	if(typeof(str)!="string") {
		return str;
	}
	if(str=="") {
		return "";
	}
	
	var isBlank = function(c) {
		return c==" " 	//半角
			|| c=="　"; 	//全角
	};
	var i=0; 
	while( i<str.length && isBlank(str.charAt(i)) ) {
		i++;
	}
	if( i == str.length ) {
		return "";
	}
	var j=str.length;
	while( j>0 && isBlank(str.charAt(j-1)) ) {
		j--;
	}
	return str.substring(i, j);
};
figurefix.prac.Tool.prototype.sort = function(arr, asce, cmpfunc) {
	if(arr==undefined || !(arr instanceof Array) || arr.length<2) {
		return arr;
	}
	
	var ascending = true;
	if(asce!=undefined && typeof(asce)=="boolean") {
		ascending = asce;
	}
		
	var cmp = typeof(cmpfunc)=="function" ? cmpfunc : function(va, vb) {
		if(va<vb) {
			return -1;
		} else if(va>vb) {
			return 1;
		} else { // equals
			return 0;
		}
	};
	
	for(var i=0; i<arr.length; i++) {
		var m = i;
		for(var j=i+1; j<arr.length; j++) {
			var cmpval = cmp(arr[j], arr[m]);
			if(( ascending && cmpval==-1 ) 
			|| ( !ascending && cmpval==1 )) {
				m = j; //find minimal or maximal
			}
		}
		if(m != i) { // move data and table rows
			var tmp = arr[i];
			arr[i] = arr[m];
			arr[m] = tmp;
		}
	}
	return arr;
};
