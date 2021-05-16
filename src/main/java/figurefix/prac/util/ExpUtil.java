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

public class ExpUtil {

	/**
	 * position an exception from the stack
	 * @param e exp
	 * @param prefix position from the stack will match this prefix
	 * @return location
	 */
	public static String position(Throwable e, String prefix) {
		if(e==null) {
			return "@";
		}
		StackTraceElement[] ste = e.getStackTrace();
		StackTraceElement loc = ste[0];
		if(prefix!=null && prefix.trim().length()>0) {
			for(int i=0; i<ste.length; i++) {
				if(ste[i].getClassName().startsWith(prefix)) {
					loc = ste[i];
					break;
				}
			}
		}

		return "@"+loc.getClassName()
				+"("+loc.getFileName()
				+":"+loc.getLineNumber()
				+")";
	}
	
	/**
	 * position an exception from the stack
	 * @param e exp
	 * @return location
	 */
	public static String position(Throwable e) {
		return position(e, null);
	}
	
	/**
	 * summarize a throwable object with position
	 * @param e exp
	 * @param prefix position from exception stack will match this prefix
	 * @return sumarization
	 */
	public static String summarize(Throwable e, String prefix) {
		if(e==null) {
			return "";
		}
		return e.toString()+position(e, prefix);
	}

	/**
	 * summarize a throwable object with position
	 * @param e exp
	 * @return sumarization
	 */
	public static String summarize(Throwable e) {
		if(e==null) {
			return "";
		}
		return e.toString()+position(e);
	}
	
	public static String getMsg(Throwable e) {
		if(e==null) {
			return "";
		} else {
			if(e.getMessage()==null 
			|| e.getMessage().trim().length()==0) {
				return e.toString();
			} else {
				return e.getMessage();
			}
		}
	}
	
	public static RuntimeException toRuntimeException(String msg, Throwable e) {
		if(msg==null || msg.trim().length()==0) {
			return toRuntimeException(e);
		} else {
			return new RuntimeException(msg.trim(), e);
		}
	}
	
	public static RuntimeException toRuntimeException(Throwable e) {
		if(e instanceof RuntimeException) {
			return (RuntimeException)e;
		}
		RuntimeException rte = new RuntimeException(e);
		return rte;
	}
}
