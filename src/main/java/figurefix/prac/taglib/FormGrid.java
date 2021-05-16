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

package figurefix.prac.taglib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import figurefix.prac.util.DataMap;
import figurefix.prac.util.DataSet;

public class FormGrid {

	private ArrayList<Column> columns = new ArrayList<Column>();
	private ArrayList<DataMap> data = null;
	
	static void testGridId(String name) {
		String regexp = "[A-Za-z0-9_]+";
		if(name==null || !name.matches(regexp)) {
			throw new RuntimeException("invalide formgrid id '"+name+"' which should matches ("+regexp+")");
		}
	}
	
	static void testColName(String name) {
		String regexp = "[A-Za-z0-9_\\|]+";
		if(name==null || !name.matches(regexp)) {
			throw new RuntimeException("invalide name '"+name+"' which should matches ("+regexp+")");
		}
	}
	
	public FormGrid() {

	}
	
	public void addColumn(Column col) {
		for(int i=0; i<this.columns.size(); i++) {
			if(this.columns.get(i).getName().equals(col.getName())) {
				throw new RuntimeException("duplicate column name '"+col.getName()+"'");
			}
		}
		this.columns.add(col);
	}
	
	public ArrayList<Column> getColumns() {
		return this.columns;
	}
	
	public ArrayList<DataMap> getData() {
		return this.data;
	}
	
	public void setData(ArrayList<DataMap> data) {
		this.data = data;
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<DataMap> bind(HttpServletRequest req, String name) {
		Object obj = req.getAttribute(name);
		if(obj instanceof ArrayList<?>) {
			ArrayList<?> list = (ArrayList<?>)obj;
			for(int i=0; i<list.size(); i++) {
				if( ! ( list.get(i) instanceof DataMap ) ) {
					throw new RuntimeException(
						"HttpServletRequest contains an ArrayList named '"+name+"', "
						+ "but at least one member of the ArrayList is not instance of "+DataMap.class.getName());
				}
			}
			return (ArrayList<DataMap>)list;
		}

		ArrayList<DataMap> datalist = new ArrayList<DataMap>();
		
		String colnames = req.getParameter(name);
		String[] colnam = null;
		if(colnames==null || colnames.trim().length()==0) {
			req.setAttribute(name, datalist);
			return datalist;
		} else {
			colnam = colnames.trim().split(",");
		}

		HashMap<Integer, DataSet> map = new HashMap<Integer, DataSet>();
		ArrayList<String> allnames = Collections.list(req.getParameterNames());
		for(int i=0; i<allnames.size(); i++) {
			String par = allnames.get(i).trim();
			if( par.startsWith(name+".") ) {
				for(int j=0; j<colnam.length; j++) {
					String cnam = colnam[j];
					String prefix = name+"."+cnam+".";
					if( par.startsWith(prefix) ) {
						int snoidx = par.lastIndexOf('.')+1;
						if( snoidx<par.length() ) {
							String snostr = par.substring( snoidx );
							if(snostr.matches("\\d+")) {
								int sno = Integer.parseInt(snostr);
								if( ! map.containsKey(sno) ) {
									map.put(sno, new DataSet());
								}
								DataSet dset = map.get(sno);
								dset.set(cnam, req.getParameter(par));
								break;
							}	
						}
					}
				}
			}
		}
		
		ArrayList<Integer> snolist = new ArrayList<Integer>();
		snolist.addAll(map.keySet());
		Collections.sort(snolist, new Comparator<Integer>(){
            public int compare(Integer a, Integer b) {
            	if(a<b) {
            		return -1;
            	} else if(a>b) {
            		return 1;
            	} else {
            		return 0;
            	}
            }
        });
//		snolist.sort(new Comparator<Integer>(){ // not supported in jdks heigher than 1.6
//            public int compare(Integer a, Integer b) {
//            	if(a<b) {
//            		return -1;
//            	} else if(a>b) {
//            		return 1;
//            	} else {
//            		return 0;
//            	}
//            }
//        });
		
		for(int i=0; i<snolist.size(); i++) {
			datalist.add( map.get( snolist.get(i) ) );
		}
		
		req.setAttribute(name, datalist);
		return datalist;
	}
}
