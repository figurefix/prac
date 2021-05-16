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

public class Base16 {

	public static final String ENCODED_CHARSET = "US-ASCII";
	
	private static byte[] encodingTable = {
			'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
			'A', 'B', 'C', 'D', 'E', 'F'};
	
	private static byte[] decodingTable = new byte[128];
	
	static {
		for(int i=0; i<encodingTable.length; i++) {
			decodingTable[encodingTable[i]] = (byte)i;
		}
	}
	
	public static String encode(byte[] data) {
		StringBuilder encoded = new StringBuilder();
		for(int i=0; i<data.length; i++) {
			byte by = data[i];
			int i1 = ( by >>> 4 ) & 0x0f;
			int i2 = by & 0x0f ;
			encoded.append( (char)(encodingTable[i1]) )
				.append( (char)(encodingTable[i2]) );
		}
		return encoded.toString();
	}
	
	public static byte[] decode(String encoded) {
		if(encoded==null || encoded.length()%2==1) {
			return null;
		}
		byte[] decoded = new byte[encoded.length()/2];
		
		for(int i=0; (i+1)<encoded.length(); i+=2) {
			char c1 = encoded.charAt(i);
			char c2 = encoded.charAt(i+1);
			byte b1 = decodingTable[c1];
			byte b2 = decodingTable[c2];
			decoded[i/2] = (byte)((b1<<4) | b2);
		}
		
		return decoded;
	}
}
