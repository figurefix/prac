/*
 * Copyright (C) 2015-2020 figurefix <figurefix@outlook.com>
 * 
 * This file is part of prac, 
 * prac is free software released under the MIT license.
 * 
 */

/*
 * simple POSITIVE fixed point number utility
 */

var figurefix = window.figurefix || {};
figurefix.prac = figurefix.prac || {};

figurefix.prac.FP2 = function() {
	
};
/*
 * num: a number or a string
 * fp: true - allow any floating point, false/undefined - fixed point as precesion 2
 */
figurefix.prac.FP2.prototype.verify = function (num, fp) {
	var one = (""+num).replace(" ", "").replace(",", "");
	if(one=="") {
		return false;
	}
	if( /^-?[\d]+.?$/.test(one) ) {
		return true;
	}
	if(typeof(fp)=="boolean" && fp) {
		if( /^-?[\d]*(\.\d+)?$/.test(one) ) {
			return true;
		}
	} else {
		if( /^-?[\d]*(\.\d{1,2})?$/.test(one) ) {
			return true;
		}		
	}
	return false;
};
/*
 * return a float with rounding on 5
 */
figurefix.prac.FP2.prototype.test = function (num) {
	if(this.verify(num, true)) {
		var one = (""+num).replace(" ", "").replace(",", "");
		var flt = parseFloat(one);
		var two = ""+flt;
		var pin = two.indexOf(".");
		if(pin!=-1 && pin<two.length-3) {
			var part1 = two.substring(0, pin+3).replace(".", "");
			var part2 = two.substring(pin+3, pin+4);
			var int1 = parseInt(part1);
			var int2 = parseInt(part2);
			if(int2>4) {
				int1 += 1;
			}
			var part = ""+int1;
			var len = part.length;
			return parseFloat( part.substring(0, len-2)+"."+part.substring(len-2) );
		} else {
			return flt;
		}
	} else {
		return 0;
	}
};
/*
 * float 2 to int
 */
figurefix.prac.FP2.prototype.mul100 = function (num) {
	var one = ""+num;
	var pin = one.indexOf(".");
	if(pin==-1) {
		return parseInt(one+"00");
	} else if(pin==one.length-2) {
		return parseInt(one.replace(".", "")+"0");
	} else { // pin==one.length-3
		return parseInt(one.replace(".", ""));
	}
};
/*
 * int to float 2
 */
figurefix.prac.FP2.prototype.div100 = function (num) {
	var nega = (""+num).indexOf("-") != -1;
	var one = ("00"+num).replace("-", ""); //keep width at least 3 
	var len = one.length;
	return parseFloat(
			(nega ? "-" : "")
			+ one.substring(0, len-2)
			+ "."
			+ one.substring(len-2)
	);
};
figurefix.prac.FP2.prototype.add = function (num1, num2) {
	var int1 = this.mul100(this.test(num1));
	var int2 = this.mul100(this.test(num2));
	return this.div100(int1+int2);
};
figurefix.prac.FP2.prototype.sub = function (num1, num2) {
	var int1 = this.mul100(this.test(num1));
	var int2 = this.mul100(this.test(num2));
	return this.div100(int1-int2);
};
figurefix.prac.FP2.prototype.gt = function (num1, num2) {
	var n1 = typeof(num1)=="number" ? num1 : parseFloat(num1);
	var n2 = typeof(num2)=="number" ? num2 : parseFloat(num2);
	return n1>n2;
};
figurefix.prac.FP2.prototype.lt = function (num1, num2) {
	var n1 = typeof(num1)=="number" ? num1 : parseFloat(num1);
	var n2 = typeof(num2)=="number" ? num2 : parseFloat(num2);
	return n1<n2;
};
figurefix.prac.FP2.prototype.eq = function (num1, num2) {
	var n1 = typeof(num1)=="number" ? num1 : parseFloat(num1);
	var n2 = typeof(num2)=="number" ? num2 : parseFloat(num2);
	return n1==n2;
};
