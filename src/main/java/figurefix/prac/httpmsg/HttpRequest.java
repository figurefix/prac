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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import figurefix.prac.util.ByteUtil;

/**
 * HTTP message client
 */
public class HttpRequest {
	
	private static SSLSocketFactory sslSocketFactory = null;
	
	private static synchronized void initSslSocketFactory() {
		if(sslSocketFactory==null) {
			SSLContext sslContext;
			try {
				sslContext = SSLContext.getInstance("SSL");
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}  
			TrustManager[] tm = {new X509TrustManager() {
				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) 
						throws CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) 
						throws CertificateException {
				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			}};
			try {
				sslContext.init(null, tm, new SecureRandom());
			} catch (KeyManagementException e) {
				throw new RuntimeException(e);
			}
			sslSocketFactory = sslContext.getSocketFactory();
		}
	}
	
	private static SSLSocketFactory getSslSocketFactory() {
		if(sslSocketFactory==null) {
			initSslSocketFactory();
		}
		return sslSocketFactory;
	}
	
	private HttpURLConnection con = null;
	private String charset = null;

	/**
	 * constructor
	 * @param url server URL (HTTP/HTTPS, UTF-8 by default)
	 */
	public HttpRequest(String url) {
		this(url, "UTF-8");
	}
	
	/**
	 * constructor
	 * @param url server URL (HTTP/HTTPS)
	 * @param charset data charset
	 */
	public HttpRequest(String url, String charset) {
		if(url==null) {
			throw new NullPointerException("null URL for constructor "+HttpRequest.class.getName());
		}
		if(charset==null) {
			throw new NullPointerException("null charset for constructor "+HttpRequest.class.getName());
		}

		try {
			URL u = new URL(url);
			String protocol = u.getProtocol();

			if("http".equals(protocol)) {
				con = (HttpURLConnection)u.openConnection();
			} else if("https".equals(protocol)) {
				HttpsURLConnection scon = (HttpsURLConnection)u.openConnection();
				scon.setSSLSocketFactory(HttpRequest.getSslSocketFactory());
				scon.setHostnameVerifier(new HostnameVerifier() {
					@Override
					public boolean verify(String arg0, SSLSession arg1) {
						return true;
					}
				});
				con = scon;
			} else {
				throw new IncorrectUrlException("unsupported URL protocol ["+protocol+"]");
			}
			con.setRequestProperty("Content-Type", "application/octet-stream; charset=utf-8");

		} catch (MalformedURLException e) {
			throw new IncorrectUrlException("incorrect URL: "+url, e);
		} catch (IOException e) {
			throw new IncorrectUrlException("incorrect URL: "+url, e);
		}
		
		this.charset = charset;
	}
	
	/**
	 * get HTTP connection
	 * @return HTTP connection
	 */
	public HttpURLConnection getConnection() {
		return this.con;
	}
	
	public void write(byte[] data) throws IOException {
		if(data!=null) {
			OutputStream os = con.getOutputStream();
			os.write(data);
			os.flush();
			os.close();				
		}
	}
	
	public byte[] read() throws IOException {
		InputStream is = con.getInputStream();
		byte[] bu = ByteUtil.read(is);
		is.close();
		return bu;
	}
	
	public byte[] readErr() throws IOException {
		InputStream is = con.getErrorStream();
		byte[] bu = ByteUtil.read(is);
		is.close();
		return bu;
	}
	
	/**
	 * connect to the url, do input and output
	 * @param method GET,POST,PUT,...
	 * @param input send data to server
	 * @param output receive data from server
	 * @param data data to be sent to server
	 * @return response data
	 * @throws IOException exp
	 */
	public byte[] connect(String method, 
			boolean input, boolean output, byte[] data) 
			throws IOException {
		
		con.setRequestMethod(method);
		con.setDoInput(input);
		con.setDoOutput(output);
		con.connect();

		if(output) {
			OutputStream os = con.getOutputStream();
			os.write(data);
			os.flush();
			os.close();			
		}
		
		byte[] bu = null;
		if(input) {
			InputStream is = con.getInputStream();
			bu = ByteUtil.read(is);
			is.close();			
		}
		con.disconnect();
		return bu;
	}
	
	/**
	 * connect to the url, do input and output with string
	 * @param method http method
	 * @param input do input
	 * @param output do output
	 * @param msg data sent to the url
	 * @return response data
	 * @throws IOException exp
	 */
	public String connect(String method, boolean input, boolean output, String msg) throws IOException {
		byte[] in = (input && msg!=null) ? URLEncoder.encode(msg, this.charset).getBytes(this.charset) : null;
		byte[] out = this.connect(method, input, output, in);
		if(output && out!=null) {
			return new String(out, this.charset);
		} else {
			return null;	
		}
	}
	
	/**
	 * request with GET method
	 * @return response data
	 * @throws IOException exp
	 */
	public byte[] doGetForBytes() throws IOException {
		return this.connect("GET", true, false, new byte[0]);
	}
	
	/**
	 * request with GET method
	 * @return response data
	 * @throws IOException exp
	 */
	public String doGet() throws IOException {
		byte[] bu = this.doGetForBytes();
		return new String(bu, this.charset);
	}
	
	/**
	 * request with POST method
	 * @param data request data
	 * @return response data
	 * @throws IOException exp
	 */
	public byte[] post(byte[] data) throws IOException {
		return this.connect("POST", true, true, data);
	}
	
	/**
	 * request with POST method
	 * @param msg request data
	 * @return response data
	 * @throws IOException exp
	 */
	public String post(String msg) throws IOException {
		return this.connect("POST", true, true, msg);
	}
}
