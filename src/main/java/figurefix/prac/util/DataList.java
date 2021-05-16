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
 * DataList is wrap a ArrayList&lt;DataSet&gt; but contains metainfo, 
 * the metainfo can be an object of any type.
 * @author figurefix
 *
 * @param <T> type
 */
public class DataList<T> {

	private T metaData = null;
	private ArrayList<DataSet> list = new ArrayList<DataSet>();
	
	public DataList() {
		
	}
	
	public DataList(T metaData) {
		this.metaData = metaData;
	}
	
	public ArrayList<DataSet> getList() {
		return this.list;
	}

	public void setMetaData(T meta) {
		this.metaData = meta;
	}
	
	public T getMetaData() {
		return this.metaData;
	}
	
	/**
	 * set the same value for every DataSet in the list
	 * @param name name
	 * @param value value
	 */
	public void allSet(String name, Object value) {
		for(int i=0; i<this.list.size(); i++) {
			this.list.get(i).set(name, value);
		}
	}
	
	/**
	 * set the same value for every DataSet in the list as a key
	 * @param name name
	 * @param value value
	 */
	public void allSetKey(String name, Object value) {
		for(int i=0; i<this.list.size(); i++) {
			this.list.get(i).setKey(name, value);
		}
	}
	
	/**
	 * remove name-value pair from every DataSet in the list
	 * @param name name
	 */
	public void allRemove(String name) {
		for(int i=0; i<this.list.size(); i++) {
			this.list.get(i).remove(name);
		}
	}
	
	/**
	 * every DataSet that matches the filter will be added to the result DataList
	 * @param filter filter
	 * @return a DataList that contains DataSets matches the filter
	 */
	public DataList<T> filter(DataFilter filter) {
		DataList<T> list = new DataList<T>(this.metaData);
		if(filter==null) {
			return list;
		}
		for(int i=0; i<this.list.size(); i++) {
			DataSet ds = this.list.get(i);
			if(filter.match(ds)) {
				list.getList().add(ds);
			}
		}
		return list;
	}
}
