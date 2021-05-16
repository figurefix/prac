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

import java.util.ArrayList;

/**
 * a queue with fixed length
 * @param <T> type
 */
public class FixedQueue<T> {

	private ArrayList<T> list;
	private int length;
	
	/**
	 * construct an empty queue
	 * @param length the length of the queue
	 */
	public FixedQueue(int length) {
		if(length<=0) {
			throw new RuntimeException("empty "+FixedQueue.class.getSimpleName()+" is not constructable");
		}
		this.length = length;
		list = new ArrayList<T>();
	}
	
	/**
	 * construct a queue with default values
	 * @param length the length of the queue
	 * @param initValue the default value to fill up the whole queue
	 */
	public FixedQueue(int length, T initValue) {
		if(length<=0) {
			throw new RuntimeException("empty "+FixedQueue.class.getSimpleName()+" is not constructable");
		}
		this.length = length;
		list = new ArrayList<T>();
		for(int i=0; i<length; i++) {
			this.add(initValue);
		}
	}
	
	/**
	 * add one element (may be null) to the tail of the queue. 
	 * if exceeded the length, the head element will be removed
	 * @param val element
	 */
	public void add(T val) {
		synchronized(this) {
			if(list.size()==this.length) {
				list.remove(0);
			}
			list.add(val);
		}
	}
	
	/**
	 * get element by the specified index
	 * @param idx from 0
	 * @return element/null
	 */
	public T get(int idx) {
		synchronized(this) {
			if(idx<list.size()) {
				return list.get(idx);
			} else {
				return null;
			}
		}
	}
	
	/**
	 * get the recent element by the specified index
	 * @param idx from 0
	 * @return element/null
	 */
	public T getRecent(int idx) {
		synchronized(this) {
			if(idx<list.size()) {
				return list.get(list.size()-1-idx);
			} else {
				return null;
			}
		}
	}
	
	public T getFirst() {
		synchronized(this) {
			if(list.size()==0) {
				return null;
			} else {
				return list.get(0);
			}
		}
	}
	
	public T getLast() {
		synchronized(this) {
			int size = list.size();
			if(size==0) {
				return null;
			} else {
				return list.get(size-1);
			}
		}
	}
}
