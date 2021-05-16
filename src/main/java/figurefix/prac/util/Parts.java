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

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;

/**
 * join multi substring into one or parts a single string into substrings
 *
 */
public final class Parts {

	private char tag = '|';
	private char esc = ':';
	
	/**
	 * default separator |<br>
	 * default escaping :
	 */
	public Parts() {
		
	}
	
	/**
	 * constructor
	 * @param tag separator char
	 * @param esc escaping char
	 */
	public Parts(char tag, char esc) {
		if(tag!=esc) {
			this.tag = tag;
			this.esc = esc;			
		} else {
			throw new RuntimeException("same charactor for tag and esc");
		}
	}
	
	/**
	 * get the separator char
	 * @return separator char
	 */
	public char getTagChar() {
		return this.tag;
	}
	
	/**
	 * get the escaping char
	 * @return escaping char
	 */
	public char getEscChar() {
		return this.esc;
	}

	public boolean isJoined(String str) {
		return str!=null && str.length()>0 && str.charAt(0)==this.tag;
	}
	
	public void joinTo(StringBuilder str, Object obj) {
		if(str==null) {
			return;
		}
		if(obj==null) {
			str.append(this.tag);
		} else if(obj instanceof Object[]) {
			Object[] arr = (Object[])obj;
			for(int i=0; i<arr.length; i++) {
				this.joinTo(str, arr[i]);
			}
		} else if(obj instanceof Iterable<?>) {
			Iterator<?> itr = ((Iterable<?>)obj).iterator();
			while(itr.hasNext()) {
				this.joinTo(str, itr.next());
			}
		} else {
			String ss = obj.toString();
			str.append(this.tag);
			for(int j=0; j<ss.length(); j++) {
				char c = ss.charAt(j);
				if(c==this.tag || c==this.esc) {
					str.append(this.esc);
				}
				str.append(c);
			}
		}
	}
	
	public String join(Object ... obj) {
		if(obj==null || obj.length==0) {
			return "";
		}
		StringBuilder str = new StringBuilder();
		for(int i=0; i<obj.length; i++) {
			this.joinTo(str, obj[i]);
		}
		return str.toString();
	}
	
	public String[] partToArray(String st) {
		Deque<String> list = this.part(st);
		return list.toArray(new String[list.size()]);
	}
	
	public void partTo(Collection<String> clc, String str) {
		if(str==null || str.length()==0 || str.charAt(0)!=this.tag) {
			return;
		}

		StringBuilder buf = new StringBuilder();
		int i = 1;
		while( i < str.length() ) {
			char c = str.charAt(i);
			if(c==this.esc) {
				buf.append(str.charAt(i+1));
				i += 2;
			} else if(c==this.tag) {
				clc.add(buf.toString());
				buf = new StringBuilder();
				i++;
			} else {
				buf.append(c);
				i++;
			}
		}
		clc.add(buf.toString());
	}
	
	public Deque<String> part(String str) {
		Deque<String> clc = new ArrayDeque<String>();
		this.partTo(clc, str);
		return clc;
	}
	
	public int getSize(String st) {
		if(!this.isJoined(st)) {
			return 0;
		}
		int size = 1;
		int i = 1;
		while( i < st.length() ) {
			char c = st.charAt(i);
			if(c==this.esc) {
				i += 2;
			} else if(c==this.tag) {
				i++;
				size++;
			} else {
				i++;
			}
		}
		return size;
	}
	
	/**
	 * get the coresponding part by index
	 * @param st parts string
	 * @param idx index that starts from 0
	 * @return the coresponding part or null (when the parameter st is not parts string or the index out of range)
	 */
	public String get(String st, int idx) {
		if(idx<0 || !this.isJoined(st)) {
			return null;
		}
		
		int cnt = -1;
		int i = 0;
		while( i < st.length() ) {
			if(cnt==idx) {
				break;
			}
			char c = st.charAt(i);
			if(c==this.esc) {
				i += 2;
			} else if(c==this.tag) {
				i++;
				cnt++;
			} else {
				i++;
			}
		}
		if(cnt==idx) {
			StringBuilder buf = new StringBuilder();
			while( i < st.length() ) {
				char c = st.charAt(i);
				if(c==this.esc) {
					buf.append(st.charAt(i+1));
					i += 2;
				} else if(c==this.tag) {
					break;
				} else {
					buf.append(c);
					i++;
				}
			}
			return buf.toString();
		}
		
		return null;
	}
}
