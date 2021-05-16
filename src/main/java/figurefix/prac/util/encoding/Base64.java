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

package figurefix.prac.util.encoding;

import java.io.UnsupportedEncodingException;

/**
 * <pre>
 * encoding chars: <code>A~Z  a~z  0~9  +  /</code>
 * supplymentary char: <code>=</code>
 * </pre>
 *
 */
public class Base64 {
	
	public static final String ENCODED_CHARSET = "US-ASCII";
	
	private static final char PADDING = '=';
	
	private static byte[] encodingTable = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 
		'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 
		'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		'+', '/'};
	
	private static byte[] decodingTable = new byte[128];
	
	static {
		for(int i=0; i<encodingTable.length; i++) {
			decodingTable[encodingTable[i]] = (byte)i;
		}		
	}
	
	public static String encode(byte[] data) {
		byte[] bytes;
		int modulus = data.length % 3;
		if (modulus == 0) {
			bytes = new byte[(4 * data.length) / 3];
		} else {
			bytes = new byte[4 * ((data.length / 3) + 1)];
		}
		
		int a1,a2,a3;
		for (int i=0, j=0; i<(data.length - modulus); i+=3, j+=4) {
			a1 = data[i] & 0xff;
			a2 = data[i+1] & 0xff;
			a3 = data[i+2] & 0xff;
			bytes[j] = encodingTable[(a1 >>> 2) & 0x3f];
			bytes[j+1] = encodingTable[((a1 << 4) | (a2 >>> 4)) & 0x3f];
			bytes[j+2] = encodingTable[((a2 << 2) | (a3 >>> 6)) & 0x3f];
			bytes[j+3] = encodingTable[a3 & 0x3f];
		}
		
		int b1,b2,b3,d1,d2;
		switch (modulus) {
		case 1:
			d1 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = (d1 << 4) & 0x3f;
			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = (byte)PADDING;
			bytes[bytes.length - 1] = (byte)PADDING;
			break;
		case 2:
			d1 = data[data.length - 2] & 0xff;
			d2 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
			b3 = (d2 << 2) & 0x3f;
			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = encodingTable[b3];
			bytes[bytes.length - 1] = (byte)PADDING;
			break;
		}

		try {
			return new String(bytes, ENCODED_CHARSET);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public static byte[] decode(String encoded) {
		byte[] data = null;
		try {
			data = encoded.getBytes(ENCODED_CHARSET);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		
		byte[] bytes;
		int pad = 0;
		if (data[data.length - 2] == PADDING) {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 1];
			pad = 2;
		} else if (data[data.length - 1] == PADDING) {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 2];
			pad = 1;
		} else {
			bytes = new byte[((data.length / 4) * 3)];
		}
		
		byte b1,b2,b3,b4;
		int times = pad==0?data.length:(data.length - 4);
		for (int i=0, j=0; i<times; i+=4, j+=3) {
			b1 = decodingTable[data[i]];
			b2 = decodingTable[data[i+1]];
			b3 = decodingTable[data[i+2]];
			b4 = decodingTable[data[i+3]];
			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j+1] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[j+2] = (byte) ((b3 << 6) | b4);
		}
		switch(pad) {
		case 2:
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];
			bytes[bytes.length - 1] = (byte) ((b1 << 2) | (b2 >> 4));
			break;
		case 1:
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];
			b3 = decodingTable[data[data.length - 2]];
			bytes[bytes.length - 2] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte) ((b2 << 4) | (b3 >> 2));
			break;
		}
		return bytes;
	}
}
