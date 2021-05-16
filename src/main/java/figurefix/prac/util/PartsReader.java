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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class PartsReader {

	private Parts parts = null;
	private InputStreamReader reader = null;
	private String charset = null;
	private int available = -1;
	
	private void init(InputStream is, String charset, Parts ps) throws UnsupportedEncodingException {
		if(is==null) {
			throw new NullPointerException("parameter InputStream");
		}
		this.charset = charset!=null ? charset : "UTF-8";
		this.parts = ps!=null ? ps : new Parts();
		reader = new InputStreamReader(is, this.charset);
	}

	public PartsReader(InputStream is, String charset, Parts ps) throws UnsupportedEncodingException {
		init(is, charset, ps);
	}
	
	public PartsReader(InputStream is) {
		try {
			init(is, null, null);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public PartsReader(InputStream is, String charset) throws UnsupportedEncodingException {
		init(is, charset, null);
	}
	
	public PartsReader(InputStream is, Parts ps) {
		try {
			init(is, null, ps);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * read the next available part.<br>  
	 * there are two circumstances that cause null return:<br>
	 * &nbsp;1.the end of the stream has been reached<br>
	 * &nbsp;2.string in the stream is not formed in Parts<br>
	 * @return the next availble part or null 
	 * @throws IOException exception
	 */
	public String read() throws IOException {
		if(this.available==-1) { // unknown
			int i = this.reader.read();
			if(i==-1 || i!=this.parts.getTagChar()) { // not parts string or encounting stream end
				this.available = 0;
			} else {
				this.available = 1;
			}
		}
		if(this.available==0) {
			return null;
		}
		StringBuilder buf = new StringBuilder();
		int c = this.reader.read();
		while(this.available==1) {
			if(c==-1) {
				this.available = 0;
				break;
			} else if(((char)c)==this.parts.getTagChar()) {
				break;
			} else if(((char)c)==this.parts.getEscChar()) {
				c = this.reader.read();
				if(c!=-1) {
					buf.append((char)c);
				} else {
					this.available = 0;
					break;
				}
			} else {
				buf.append((char)c);
			}
			c = this.reader.read();
		}
		return buf.toString();
	}

	public Parts getParts() {
		return parts;
	}

	public String getCharset() {
		return charset;
	}
}
