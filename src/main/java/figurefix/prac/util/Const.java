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

import java.util.HashMap;

/**
 * constant, whose value can be integer or string
 */
public final class Const {
	
	private static HashMap<String, HashMap<String, Const>> all 
		= new HashMap<String, HashMap<String, Const>>();
	
	private static synchronized HashMap<String, Const> findGroup(Class<?> cls) {
		if(cls==null) {
			throw new NullPointerException("constant group value");
		}
		String group = cls.getName();
		if(!all.containsKey(group)) {
			all.put(group, new HashMap<String, Const>());
		}
		return all.get(group);
	}

	public static Const create(Class<?> group, String value, String name) {
		Const cnst = new Const(value, name);
		findGroup(group).put(value, cnst);
		return cnst;
	}
	
	public static Const create(Class<?> group, int value, String name) {
		return create(group, String.valueOf(value), name);
	}
	
	public static String getName(Class<?> group, String value) {
		HashMap<String, Const> list = all.get(group.getName());
		if(list!=null) {
			Const cnst = list.get(value);
			if(cnst!=null) {
				return cnst.name();
			}
		}
		return null;
	}
	
	public static String getName(Class<?> group, int value) {
		return getName(group, String.valueOf(value));
	}
	
	private boolean isIntValue = false;
	private String vv = null;
	private String nn = null;
	
	private Const(String value, String name) {
		if(value==null) {
			throw new NullPointerException("constant value");
		}
		this.vv = value;
		this.nn = name;
	}
	
	private Const(int value, String name) {
		this(""+value, name);
		this.isIntValue = true;
	}
	
	public String name() {
		return this.nn;
	}
	
	public String value() {
		return this.vv;
	}
	
	public int intValue() {
		return Integer.parseInt(this.vv);
	}
	
	public boolean equals(int value) {
		return this.equals(""+value);
	}
	
	public boolean equals(String value) {
		return this.vv!=null && this.vv.equals(value);
	}

	public boolean isInt() {
		return this.isIntValue;
	}

	public String toString() {
		return this.vv;
	}
}
