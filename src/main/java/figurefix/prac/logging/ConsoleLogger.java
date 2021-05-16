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

package figurefix.prac.logging;

import java.util.logging.Level;

import figurefix.prac.util.DateTime;

/**
 * a Logger implementation that log to console
 */
public class ConsoleLogger implements Logger {

	static String attachTime(Level lvl, String msg) {
		StringBuilder timed = new StringBuilder();
		timed.append('[')
			.append(DateTime.now().toString())
			.append("] ")
			.append(lvl.getLocalizedName()).append(": ")
			.append(msg);
		return timed.toString();
	}

	@Override
	public void trace(String msg) {
		System.out.println(attachTime(Level.FINE, msg));
	}

	@Override
	public void info(String msg) {
		System.out.println(attachTime(Level.INFO, msg));
	}
	
	@Override
	public void error(String msg) {
		System.err.println(attachTime(Level.SEVERE, msg));
	}

}
