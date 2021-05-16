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

package figurefix.prac.taglib.html;

import java.util.HashMap;
import java.util.Set;

public final class AttributeList {

	private HashMap<String, String> map = new HashMap<String, String>();
	
	public AttributeList() {
		
	}
	
	public String[] getAllNames() {
		Set<String> set = this.map.keySet();
		return set.toArray(new String[set.size()]);
	}
	
	public void set(String name) {
		this.set(name, null);
	}
	
	public void set(String name, String value) {
		if(name!=null && name.trim().length()>0) {
			this.map.put(name.trim().toLowerCase(), value);			
		}
	}
	
	public void set(AttributeList attr) {
		if(attr!=null) {
			this.map.putAll(attr.map);			
		}
	}
	
	public void remove(String name) {
		if(name!=null) {
			this.map.remove(name.trim().toLowerCase());			
		}
	}
	
	public void empty() {
		this.map.clear();
	}
	
	public String get(String name) {
		return name==null ? null : this.map.get(name.trim().toLowerCase());
	}
	
	public boolean contains(String name) {
		return name==null ? false : this.map.containsKey(name.trim().toLowerCase());
	}
	
	public int size() {
		return this.map.size();
	}
	
	public String toString() {
		if(this.map.size()==0) {
			return "";
		}
		Set<String> keyset = this.map.keySet();
		String[] keys = keyset.toArray(new String[keyset.size()]);
		StringBuilder list = new StringBuilder();
		for(int i=0; i<keys.length; i++) {
			String val = this.map.get(keys[i]);
			if(val==null) {
				list.append(" ").append(keys[i]);
			} else {
				list.append(" ").append(keys[i])
				.append("='").append(val).append("'");
			}
		}
		return list.toString();
	}
}
