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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * byte tool
 */
public class ByteUtil {

	/**
	 * read all bytes from input stream
	 * @param is input stream
	 * @return bytes
	 * @throws IOException exp
	 */
	public static byte[] read(InputStream is) throws IOException {
		ArrayList<byte[]> arr = new ArrayList<byte[]>();
		byte[] bu = new byte[1024];
		int idx = 0;
		int cnt = 0;
		do {
			idx += cnt;
			if(idx==bu.length) {
				arr.add(bu);
				bu = new byte[1024];
				idx = 0;
			}
			cnt = is.read(bu, idx, bu.length-idx);
		} while (cnt != -1);
		
		byte[] buff = new byte[arr.size() * 1024 + idx];
		int n = 0;
		for(int i=0; i<arr.size(); i++) {
			byte[] ba = arr.get(i);
			for(int j=0; j<ba.length; j++) {
				buff[n++] = ba[j];
			}
		}
		for(int i=0; i<idx; i++) {
			buff[n++] = bu[i];
		}
		return buff;
	}

	public static String toBinaryString(byte b) {
		StringBuilder buffer = new StringBuilder();
		int mask = 128;
		while(mask>0) {
			int tmp = b & mask;
			buffer.append( tmp==0 ? "0" : "1" );
			mask /= 2;
		}
		return buffer.toString();
	}
	
	public static String toHexString(byte b) {
		String binstr = toBinaryString(b);
		int up = Integer.parseInt(binstr.substring(0, 4), 2);
		int low = Integer.parseInt(binstr.substring(4), 2);
		return ( Integer.toHexString(up) + Integer.toHexString(low) ).toUpperCase();
	}
	
	public static String toBinHexString(byte b) {
		String binstr = toBinaryString(b);
		int up = Integer.parseInt(binstr.substring(0, 4), 2);
		int low = Integer.parseInt(binstr.substring(4), 2);
		return binstr + " " + 
				( Integer.toHexString(up) + Integer.toHexString(low) ).toUpperCase();		
	}

	public static void print(byte[] barr, int colnum, OutputStream os, String charset) throws UnsupportedEncodingException, IOException {
		if(colnum<=0) {
			colnum = 1;
		}
		
		byte[] lnbrk = charset==null ? "\n".getBytes() : "\n".getBytes(charset);
		int col = colnum;
		for(int i=0; i<barr.length; i++) {
			String val = toBinHexString(barr[i])+" ";
			os.write(charset==null ? val.getBytes() : val.getBytes(charset));
			col--;
			if(col==0 && i!=barr.length-1) {
				os.write(lnbrk);
				col = colnum;
			}
		}
		os.write(lnbrk);
		os.flush();
	}

	public static void print(byte[] barr, int colnum) {
		try {
			print(barr, colnum, System.out, null);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public static void print(byte[] barr) {
		print(barr, 1);
	}
}
