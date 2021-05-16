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

package figurefix.prac.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * a convenient wrapper of BigDecimal
 */
public class AnyDecimal {

	private BigDecimal decimal = null;
	
	public AnyDecimal() {
		this.decimal = new BigDecimal("0");
	}
	
	public AnyDecimal(Object val) {
		this.decimal = this.toBigDecimal(val);
	}
	
	private BigDecimal parse(String val) {
		String vv = val.replaceAll("[^\\d\\.]", "");
		return new BigDecimal(vv);
	}
	
	private BigDecimal toBigDecimal(Object val) {
		if(val==null) {
			return new BigDecimal("0");
		} else if(val instanceof AnyDecimal) {
			return ((AnyDecimal)val).decimal;
		} else if(val instanceof BigDecimal) {
			return (BigDecimal)val;
		} else if(val instanceof String) {
			return this.parse((String)val);
		} else {
			return this.parse(val.toString());
		}
	}
	
	public synchronized AnyDecimal add(Object val) {
		return new AnyDecimal(this.decimal.add(this.toBigDecimal(val)));
	}
	
	public synchronized AnyDecimal sub(Object val) {
		return new AnyDecimal(this.decimal.subtract(this.toBigDecimal(val)));
	}
	
	public synchronized AnyDecimal multiply(Object val) {
		return new AnyDecimal(this.decimal.multiply(this.toBigDecimal(val)));
	}
	
	public synchronized AnyDecimal divide(Object val) {
		return this.divide(val, 3);
	}
	
	public synchronized AnyDecimal divide(Object val, int scale) {
		return new AnyDecimal(this.decimal.divide(
				this.toBigDecimal(val), scale, RoundingMode.HALF_UP));
	}

	public boolean eq(Object val) {
		return this.decimal.compareTo(this.toBigDecimal(val))==0;
	}
	
	public boolean gt(Object val) {
		return this.decimal.compareTo(this.toBigDecimal(val))>0;
	}
	
	public boolean lt(Object val) {
		return this.decimal.compareTo(this.toBigDecimal(val))<0;
	}
	
	public BigDecimal toBigDecimal() {
		return this.decimal;
	}
	
	public AnyDecimal setScale(int scale) {
		return new AnyDecimal(this.decimal.setScale(scale, RoundingMode.HALF_UP));
	}
	
	public String toString() {
		return this.toString(2);
	}
	
	public String toString(int scale) {
		return this.decimal.setScale(scale, BigDecimal.ROUND_HALF_UP).toPlainString();
	}

	public String toPlainString() {
		return this.decimal.toPlainString();
	}
}
