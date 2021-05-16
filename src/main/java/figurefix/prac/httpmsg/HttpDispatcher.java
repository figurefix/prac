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

package figurefix.prac.httpmsg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import figurefix.prac.util.ByteUtil;

/**
 * HTTP message dispatcher
 */
public class HttpDispatcher {

	private HttpServletRequest req = null;
	private HttpServletResponse rsp = null;
	private String charset = null;
	
	/**
	 * constructor
	 * @param req request
	 * @param rsp response
	 * @param charset data charset
	 */
	public HttpDispatcher(HttpServletRequest req, HttpServletResponse rsp, String charset) {
		if(req==null || rsp==null || charset==null) {
			throw new NullPointerException(
					"null parameter for constructor of "+HttpDispatcher.class.getName());
		}
		this.req = req;
		this.rsp = rsp;
		this.charset = charset;
	}
	
	/**
	 * constructor (UTF-8 by default)
	 * @param req request
	 * @param rsp response
	 */
	public HttpDispatcher(HttpServletRequest req, HttpServletResponse rsp) {
		this(req, rsp, "UTF-8");
	}
	
	/**
	 * get data from client
	 * @return request data
	 * @throws IOException exp
	 */
	public byte[] receiveBytes() throws IOException {
		InputStream is = req.getInputStream();
		byte[] bu = ByteUtil.read(is);
		return bu;
	}
	
	/**
	 * get data from client
	 * @return request data
	 * @throws IOException exp
	 */
	public String receive() throws IOException {
		req.setCharacterEncoding(this.charset);
		byte[] bu = this.receiveBytes();
		String s = new String(bu, this.charset);
		return URLDecoder.decode(s, this.charset);
	}
	
	/**
	 * send data to client
	 * @param data response data
	 * @throws IOException exp
	 */
	public void reply(byte[] data) throws IOException {
		rsp.setContentLength(data.length);
		OutputStream os = rsp.getOutputStream();
		os.write(data);
		os.flush();
		os.close();
	}
	
	/**
	 * send data to client
	 * @param s response data
	 * @throws IOException exp
	 */
	public void reply(String s) throws IOException {
		if(s!=null) {
			rsp.setCharacterEncoding(this.charset);
			this.reply(s.getBytes(this.charset));			
		}
	}
}
