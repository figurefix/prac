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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

/**
 * a ClearLog implementation that log to file
 * @author figurefix
 *
 */
public class FileLogger implements Logger {
	
	private BufferedWriter bw = null;
	
	/**
	 * create file logger
	 * @param file absolute file path
	 */
	public FileLogger(String file) {
		if(file==null || file.trim().length()==0) {
			throw new IllegalArgumentException("empty file name");
		}
		try {
			int i = file.length()-1;
			char c = file.charAt(i);
			while(c!='/' && c!='\\' && i>0) {
				c = file.charAt(--i);
			}
			if((c=='/' || c=='\\' ) && i>0) {
				File path = new File(file.substring(0, i));
				if(!path.exists()) {
					path.mkdirs();
				}
			}
			File fi = new File(file);
			if(!fi.exists()) {
				fi.createNewFile();
			}
			FileWriter fw = new FileWriter(fi, true);
			bw = new BufferedWriter(fw);
		} catch (IOException e) {
			throw new RuntimeException("failed to create file ("+file+")", e);
		}
	}
	
	private void dolog(Level lvl, String msg) {
		try {
			bw.write("\n"+ConsoleLogger.attachTime(lvl, msg));
			bw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void trace(String msg) {
		dolog(Level.FINE, msg);
	}
	
	@Override
	public void info(String msg) {
		dolog(Level.INFO, msg);
	}

	@Override
	public void error(String msg) {
		dolog(Level.SEVERE, msg);
	}

	public void finalize() {
		try {
			bw.flush();
			bw.close();
		} catch (Throwable e) {
			
		}
	}

}
